package org.scalingmq.storage.core.replicate.raft;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.lifecycle.Lifecycle;
import org.scalingmq.common.net.NetworkClient;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.PartitionMsgStorage;
import org.scalingmq.storage.core.replicate.raft.entity.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGeneratorFactory;

/**
 * raft算法实现
 *
 * @author renyansong
 */
@Slf4j
public class RaftCore implements Lifecycle {

    private static final RaftCore INSTANCE = new RaftCore();

    /**
     * state的更新锁
     */
    private static final Object STATE_LOCK = new Object();

    /**
     * 投票的最小间隔时间 毫秒
     */
    private static final int MIN_VOTE_TIME_INTERVAL_MS = 300;

    /**
     * 投票的最大间隔时间 毫秒
     */
    private static final int MAX_VOTE_TIME_INTERVAL_MS = 500;

    /**
     * leader的心跳间隔
     */
    private static final int HEARTBEAT_INTERVAL_MS = 1000;

    /**
     * 最大缺少心跳的次数
     */
    private static final int MAX_MISSING_HEARTBEAT_COUNT = 3;

    /**
     * 访问其他节点的并发线程池
     * 会按照节点的大小动态调节线程数量
     */
    private static final ThreadPoolExecutor CALL_PEER_THREAD_POOL = new ThreadPoolExecutor(0,
            0, 0L,
            TimeUnit.MILLISECONDS,
            new SynchronousQueue<>(),
            new ThreadFactory() {
                final AtomicInteger index = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "raft-call-peer-thread" + index.getAndIncrement());
                }
            });

    /**
     * leader的心跳定时任务
     */
    private static final ScheduledThreadPoolExecutor HEARTBEAT_THREAD_POOL
            = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "raft-leader-heartbeat-thread"));

    /**
     * 检测leader心跳是否过期的定时任务
     */
    private static final ScheduledThreadPoolExecutor HEARTBEAT_TIME_CHECK_THREAD_POOL
            = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "raft-follower-heartbeat-check-thread"));

    /**
     * 当前节点的角色 默认候选者
     */
    private RaftStateEnum state = RaftStateEnum.CANDIDATE;

    /**
     * 当前节点的期数
     */
    private long concurrentTerm = 0;

    /**
     * 当前节点ID
     */
    private int peerId;

    /**
     * leader节点ID
     */
    private int leaderPeerId = -1;

    /**
     * 上次收到心跳的时间
     */
    private volatile long lastReceiveHeartbeatTime;

    /**
     * 心跳任务handler
     */
    private ScheduledFuture<?> heartbeatFuture;

    /**
     * 检测心跳任务handler
     */
    private ScheduledFuture<?> heartbeatCheckFuture;

    /**
     * 重新投票标识
     */
    private boolean reElectFlag = false;

    public static RaftCore getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("AlibabaSwitchStatement")
    private void init() {
        // 初始话一次当前节点情况
        CALL_PEER_THREAD_POOL.setCorePoolSize(PeerFinder.getInstance().getPeers().size());
        CALL_PEER_THREAD_POOL.setMaximumPoolSize(PeerFinder.getInstance().getPeers().size());

        // 订阅节点变动
        PeerFinder.getInstance().listenPeer(peerSet -> {
            CALL_PEER_THREAD_POOL.setCorePoolSize(peerSet.size());
            CALL_PEER_THREAD_POOL.setMaximumPoolSize(peerSet.size());
        });

        // 读取配置
        StorageConfig storageConfig = StorageConfig.getInstance();
        String hostname = storageConfig.getHostname();
        if (hostname != null && !"".equals(hostname)) {
            peerId = Integer.parseInt(hostname.split("-")[1]);
        } else {
            return;
        }
        if (storageConfig.getCoordinatorNums() != null && !"".equals(storageConfig.getCoordinatorNums())) {
            String[] coordinators = storageConfig.getCoordinatorNums().split(",");
            List<String> coordinatorList = Arrays.asList(coordinators);
            if (coordinatorList.contains(String.valueOf(peerId))) {
                // 当前节点是协调节点
                state = RaftStateEnum.COORDINATOR;
            }
        }
        // 角色判断
        switch (state) {
            case COORDINATOR -> doCoordinator();
            case CANDIDATE -> doCandidate();
            default -> throw new RuntimeException("bug report");
        }
    }

    /**
     * 协调者逻辑
     */
    private void doCoordinator() {

    }

    /**
     * 候选者逻辑
     */
    private void doCandidate() {
        // 发送选票
        sendVote(false, true);
    }

    /**
     * 发送选票 解析结果
     */
    private void sendVote(boolean immediately, boolean untilSuccess) {
        synchronized (STATE_LOCK) {
            if (state != RaftStateEnum.CANDIDATE && state != RaftStateEnum.FOLLOWER) {
                return;
            }
        }
        if (!immediately) {
            int sleepTime = RandomGeneratorFactory.getDefault().create(System.currentTimeMillis())
                    .ints(1, MIN_VOTE_TIME_INTERVAL_MS, MAX_VOTE_TIME_INTERVAL_MS).toArray()[0];
            try {
                TimeUnit.MILLISECONDS.sleep(sleepTime);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        // 构造投票请求
        RaftVoteReqWrapper.RaftVoteReq raftVoteReq
                = RaftVoteReqWrapper.RaftVoteReq.newBuilder()
                // 初始投票给自己
                .setLeaderId(peerId)
                .setTerm(concurrentTerm)
                .build();

        RaftReqWrapper.RaftReq raftReq
                = RaftReqWrapper.RaftReq.newBuilder()
                .setReqType(RaftReqWrapper.RaftReq.ReqType.VOTE)
                .setVoteReq(raftVoteReq)
                .build();

        // 向所有的客户端发起选票请求
        List<Future<RaftResWrapper.RaftRes>> futureList = new ArrayList<>();
        for (String peer : PeerFinder.getInstance().getPeers()) {
            if (PeerFinder.getInstance().isSelf(peer)) {
                continue;
            }

            Future<RaftResWrapper.RaftRes> raftResFuture = CALL_PEER_THREAD_POOL.submit(new CallPeerTask(raftReq, peer));
            futureList.add(raftResFuture);
        }
        // 收到的票数，自己先加一票
        int voteNums = 1;
        while (futureList.size() != 0) {
            Iterator<Future<RaftResWrapper.RaftRes>> iterator = futureList.iterator();
            while (iterator.hasNext()) {
                Future<RaftResWrapper.RaftRes> raftResFuture = iterator.next();
                if (raftResFuture.isDone()) {
                    RaftResWrapper.RaftRes raftRes = null;
                    try {
                        raftRes = raftResFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                    // 处理请求
                    if (raftRes != null) {
                        RaftVoteResWrapper.RaftVoteRes raftVoteRes = raftRes.getRaftVoteRes();
                        if (raftVoteRes.getResult().equals(RaftVoteResWrapper.RaftVoteRes.Result.ACCEPT)) {
                            voteNums++;
                        } else if (raftVoteRes.getResult().equals(RaftVoteResWrapper.RaftVoteRes.Result.TERM_EXPIRE)) {
                            // 检查是不是自己 重启的场景
                            concurrentTerm = raftVoteRes.getTerm();
                            if (raftVoteRes.getLeaderId() == leaderPeerId) {
                                becomeLeader();
                            }
                        }
                        iterator.remove();
                    }
                }
            }
        }

        // 是否票数过半
        if (isQuorum(voteNums)) {
            becomeLeader();
            return;
        }

        if (untilSuccess) {
            // 继续投票
            sendVote(immediately, untilSuccess);
        }
    }

    /**
     * 收到选票请求
     */
    public RaftResWrapper.RaftRes receiveVote(RaftVoteReqWrapper.RaftVoteReq req) {
        synchronized (STATE_LOCK) {
            // 检查期数
            if (req.getTerm() > concurrentTerm) {
                // 当前节点期数过期了
                // 认可选票的leader
                concurrentTerm = req.getTerm();
                leaderPeerId = req.getLeaderId();
                becomeFollower();

                RaftVoteResWrapper.RaftVoteRes raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                        .setResult(RaftVoteResWrapper.RaftVoteRes.Result.ACCEPT)
                        .build();
                return RaftResWrapper.RaftRes.newBuilder()
                        .setRaftVoteRes(raftVoteRes)
                        .build();
            }
            if (req.getTerm() < concurrentTerm) {
                // 过期的选票
                RaftVoteResWrapper.RaftVoteRes raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                        .setResult(RaftVoteResWrapper.RaftVoteRes.Result.TERM_EXPIRE)
                        .setLeaderId(leaderPeerId)
                        .setTerm(concurrentTerm)
                        .build();
                return RaftResWrapper.RaftRes.newBuilder()
                        .setRaftVoteRes(raftVoteRes)
                        .build();
            }
            // 同期选票
            // 如果当前节点没有投票 就把票投给对应的节点
            if (leaderPeerId == -1) {
                leaderPeerId = req.getLeaderId();
                becomeFollower();

                RaftVoteResWrapper.RaftVoteRes raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                        .setResult(RaftVoteResWrapper.RaftVoteRes.Result.ACCEPT)
                        .build();
                return RaftResWrapper.RaftRes.newBuilder()
                        .setRaftVoteRes(raftVoteRes)
                        .build();
            }
            // 检测心跳是否正常
            if (lastReceiveHeartbeatTime + MAX_MISSING_HEARTBEAT_COUNT * HEARTBEAT_INTERVAL_MS < System.currentTimeMillis()) {
                // 没有收到心跳太久
                if (reElectFlag) {
                    // 已经重新投了
                    RaftVoteResWrapper.RaftVoteRes raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                            .setResult(RaftVoteResWrapper.RaftVoteRes.Result.REJECT)
                            .build();
                    return RaftResWrapper.RaftRes.newBuilder()
                            .setRaftVoteRes(raftVoteRes)
                            .build();
                }
                // 认可选票
                reElectFlag = true;
                leaderPeerId = req.getLeaderId();
                RaftVoteResWrapper.RaftVoteRes raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                        .setResult(RaftVoteResWrapper.RaftVoteRes.Result.ACCEPT)
                        .build();
                return RaftResWrapper.RaftRes.newBuilder()
                        .setRaftVoteRes(raftVoteRes)
                        .build();
            }

            // 已经投票了 拒绝选票
            RaftVoteResWrapper.RaftVoteRes raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                    .setResult(RaftVoteResWrapper.RaftVoteRes.Result.REJECT)
                    .build();
            return RaftResWrapper.RaftRes.newBuilder()
                    .setRaftVoteRes(raftVoteRes)
                    .build();
        }

    }

    /**
     * 收到心跳请求
     */
    public RaftResWrapper.RaftRes receiveHeartbeat(RaftHeartbeatReqWrapper.RaftHeartbeatReq req) {
        if (req.getTerm() < concurrentTerm) {
            // 收到低于任期的心跳
            return RaftResWrapper.RaftRes.newBuilder()
                    .setRaftHeartbeatRes(
                            RaftHeartbeatResWrapper.RaftHeartbeatRes.newBuilder()
                                    .setResType(RaftHeartbeatResWrapper.RaftHeartbeatRes.ResponseType.TERM_EXPIRED)
                                    .setLeaderId(leaderPeerId)
                                    .setTerm(concurrentTerm)
                    )
                    .build();
        }
        // 正常反馈收到心跳 重新计时收到心跳的时间
        lastReceiveHeartbeatTime = System.currentTimeMillis();
        return RaftResWrapper.RaftRes.newBuilder()
                .setRaftHeartbeatRes(
                        RaftHeartbeatResWrapper.RaftHeartbeatRes.newBuilder()
                                .setResType(RaftHeartbeatResWrapper.RaftHeartbeatRes.ResponseType.OK)
                )
                .build();
    }

    /**
     * 成为Leader
     */
    private void becomeLeader() {
        synchronized (STATE_LOCK) {
            log.debug("当前节点:{}, 成为Leader, 期数:{}", peerId, concurrentTerm);
            reElectFlag = false;
            if (state == RaftStateEnum.FOLLOWER) {
                log.debug("当前节点:{}, 从follower变成Leader", peerId);
                if (heartbeatCheckFuture != null) {
                    heartbeatCheckFuture.cancel(true);
                }
            }
            state = RaftStateEnum.LEADER;
            // Leader 心跳逻辑 检查数据逻辑
            // TODO: 2022/9/24 检查数据

            if (concurrentTerm != 0L) {
                // 每次leader变更都需要增加任期
                concurrentTerm++;
            }
            // 发送心跳
            heartbeatFuture
                    = HEARTBEAT_THREAD_POOL.scheduleWithFixedDelay(
                    new HeartbeatTask(),
                    0L,
                    HEARTBEAT_INTERVAL_MS,
                    TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 成为Follower
     */
    private void becomeFollower() {
        synchronized (STATE_LOCK) {
            log.debug("当前节点:{}, 成为Follower, 期数:{}", peerId, concurrentTerm);
            reElectFlag = false;
            // 检查是不是从Leader变过来的
            if (state == RaftStateEnum.LEADER) {
                log.debug("节点:{}, 从Leader变为Follower", peerId);
                if (heartbeatFuture != null) {
                    heartbeatFuture.cancel(true);
                }
            }
            state = RaftStateEnum.FOLLOWER;
            // 启动一个定时任务 监听leader的心跳是否超时
            heartbeatCheckFuture = HEARTBEAT_TIME_CHECK_THREAD_POOL.scheduleWithFixedDelay(() -> {
                // 超过三次没有收到心跳
                if (lastReceiveHeartbeatTime + MAX_MISSING_HEARTBEAT_COUNT * HEARTBEAT_INTERVAL_MS < System.currentTimeMillis()) {
                    reElect();
                }
            }, 0L, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
        }
    }

    private void reElect() {
        synchronized (STATE_LOCK) {
            reElectFlag = true;
            // 发起选票
            sendVote(false, false);
            // 每轮投票都检测
            synchronized (STATE_LOCK) {
                if (state == RaftStateEnum.LEADER) {
                    return;
                }
                // 检测心跳是否恢复
                if (lastReceiveHeartbeatTime + MAX_MISSING_HEARTBEAT_COUNT * HEARTBEAT_INTERVAL_MS < System.currentTimeMillis()) {
                    // 没有恢复
                    reElect();
                }
            }
        }
    }

    private boolean isQuorum(int nums) {
        return nums > PeerFinder.getInstance().getPeers().size();
    }

    /**
     * leader逻辑
     */
    private void doLeader() {

    }

    /**
     * 当前应用是不是Leader角色
     */
    public boolean isLeader() {
        return state.equals(RaftStateEnum.LEADER);
    }

    @Override
    public void componentStart() {
        init();
    }

    @Override
    public void componentStop() {

    }

    /**
     * 访问其他节点的异步任务
     */
    private record CallPeerTask(Object raftReq, String peer) implements Callable<RaftResWrapper.RaftRes> {

        /**
         * 异步请求
         *
         * @return raft 响应
         */
        @Override
        public RaftResWrapper.RaftRes call() {
            return (RaftResWrapper.RaftRes) NetworkClient.getInstance()
                    .sendReq(raftReq,
                            peer,
                            StorageConfig.RAFT_PORT,
                            RaftVoteReqWrapper.RaftVoteReq.getDefaultInstance());
        }

    }

    /**
     * 心跳任务
     */
    private static class HeartbeatTask implements Runnable {

        @Override
        public void run() {
            // 构造心跳
            RaftHeartbeatReqWrapper.RaftHeartbeatReq heartbeatReq = RaftHeartbeatReqWrapper.RaftHeartbeatReq.newBuilder()
                    .setMaxOffset(PartitionMsgStorage.getInstance().getGlobalIndexWrote())
                    .setLeaderId(getInstance().leaderPeerId)
                    .setTerm(getInstance().concurrentTerm)
                    .build();

            RaftReqWrapper.RaftReq req = RaftReqWrapper.RaftReq.newBuilder()
                    .setHeartbeatReq(heartbeatReq)
                    .build();

            // 向所有的节点发送心跳
            List<Future<RaftResWrapper.RaftRes>> futureList = new ArrayList<>();
            for (String peer : PeerFinder.getInstance().getPeers()) {
                if (PeerFinder.getInstance().isSelf(peer)) {
                    continue;
                }

                Future<RaftResWrapper.RaftRes> raftResFuture = CALL_PEER_THREAD_POOL.submit(new CallPeerTask(req, peer));
                futureList.add(raftResFuture);
            }

            while (futureList.size() != 0) {
                Iterator<Future<RaftResWrapper.RaftRes>> iterator = futureList.iterator();
                while (iterator.hasNext()) {
                    Future<RaftResWrapper.RaftRes> raftResFuture = iterator.next();
                    if (raftResFuture.isDone()) {
                        RaftResWrapper.RaftRes raftRes = null;
                        try {
                            raftRes = raftResFuture.get();
                        } catch (InterruptedException | ExecutionException e) {
                            // ignore
                        }
                        // 处理请求
                        if (raftRes != null) {
                            RaftHeartbeatResWrapper.RaftHeartbeatRes raftHeartbeatRes = raftRes.getRaftHeartbeatRes();
                            if (raftHeartbeatRes.getResType().equals(RaftHeartbeatResWrapper.RaftHeartbeatRes.ResponseType.TERM_EXPIRED)) {
                                synchronized (STATE_LOCK) {
                                    // 当前Leader过期了
                                    getInstance().leaderPeerId = raftHeartbeatRes.getLeaderId();
                                    getInstance().concurrentTerm = raftHeartbeatRes.getTerm();
                                    getInstance().becomeFollower();
                                }

                            }
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

}
