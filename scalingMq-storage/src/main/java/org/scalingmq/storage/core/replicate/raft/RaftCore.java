package org.scalingmq.storage.core.replicate.raft;

import com.google.protobuf.MessageLite;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.ioc.IocContainer;
import org.scalingmq.common.lifecycle.Lifecycle;
import org.scalingmq.common.net.NetworkClient;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.PartitionMsgStorage;
import org.scalingmq.storage.core.replicate.raft.entity.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * raft算法实现
 *
 * @author renyansong
 */
@Slf4j
public class RaftCore implements Lifecycle {

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
    private static final int MAX_VOTE_TIME_INTERVAL_MS = 1500;

    /**
     * leader的心跳间隔
     */
    private static final int HEARTBEAT_INTERVAL_MS = 5000;

    /**
     * 最大缺少心跳的次数
     */
    private static final int MAX_MISSING_HEARTBEAT_COUNT = 3;

    /**
     * 访问其他节点的并发线程池
     * 会按照节点的大小动态调节线程数量
     */
    private static final ThreadPoolExecutor CALL_PEER_THREAD_POOL = new ThreadPoolExecutor(1,
            1, 0L,
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
    private volatile long concurrentTerm = 0;

    /**
     * 当前节点ID
     */
    private int peerId;

    /**
     * leader节点ID
     */
    private volatile int leaderPeerId = -1;

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

    @SuppressWarnings("AlibabaSwitchStatement")
    private void init() {
        log.debug("raft 开始初始化....");
        // 初始话一次当前节点情况
        int peerCount = IocContainer.getInstance().getObj(PeerFinder.class).getPeers().size();
        log.debug("当前节点数量:{}", peerCount);
        CALL_PEER_THREAD_POOL.setMaximumPoolSize(peerCount);
        CALL_PEER_THREAD_POOL.setCorePoolSize(peerCount);

        // 订阅节点变动
        IocContainer.getInstance().getObj(PeerFinder.class).listenPeer(peerSet -> {
            CALL_PEER_THREAD_POOL.setMaximumPoolSize(peerSet.size());
            CALL_PEER_THREAD_POOL.setCorePoolSize(peerSet.size());
        });

        // 读取配置
        StorageConfig storageConfig = StorageConfig.getInstance();
        String hostname = storageConfig.getHostname();
        if (hostname != null && !"".equals(hostname)) {
            String[] split = hostname.split("-");
            peerId = Integer.parseInt(split[split.length - 1]);
        } else {
            return;
        }
        if (storageConfig.getCoordinatorNums() != null && !"".equals(storageConfig.getCoordinatorNums())) {
            String[] coordinators = storageConfig.getCoordinatorNums().split(",");
            List<String> coordinatorList = Arrays.asList(coordinators);
            if (coordinatorList.contains(String.valueOf(peerId))) {
                // 当前节点是协调节点
                state = RaftStateEnum.COORDINATOR;
                log.debug("当前节点:{} 是协调节点, 不参与选举", peerId);
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
        // 等待心跳即可
        // TODO: 2022/9/24 补充协调节点逻辑 不被发选票
    }

    /**
     * 候选者逻辑
     */
    private void doCandidate() {
        log.debug("开始进行候选者逻辑...");
        // 发送选票
        sendVote(true, true);
    }

    /**
     * 发送选票 解析结果
     */
    private void sendVote(boolean untilSuccess, boolean addTerm) {
        // 记录投票前的leaderId
        int oldLeaderId;
        if (leaderPeerId == -1) {
            oldLeaderId = peerId;
        } else {
            oldLeaderId = leaderPeerId;
        }

        while (true) {
            log.debug("开始发送选票, 选定的leaderId:{}", leaderPeerId);
            int sleepTime = new Random().nextInt(MIN_VOTE_TIME_INTERVAL_MS, MAX_VOTE_TIME_INTERVAL_MS);
            log.debug("当前节点:{}, 选票发送睡眠时间:{}ms", peerId, sleepTime);
            try {
                TimeUnit.MILLISECONDS.sleep(sleepTime);
            } catch (InterruptedException e) {
                // ignore
            }

            List<Future<RaftResWrapper.RaftRes>> futureList;

            synchronized (STATE_LOCK) {
                // 两种情况
                // 1. 获取锁之后，发现不是初始化情况，leader id 和之前记录的不一样了
                // 2. 不是重选举模式下，leaderId不是本身了
                if ((leaderPeerId != -1 && leaderPeerId != oldLeaderId)
                        || (!addTerm && leaderPeerId != peerId)) {
                    log.debug("投票期间leader发生更改, 不需要发送选票了");
                    return;
                }
                // 修改Leader id
                leaderPeerId = peerId;
                if (addTerm) {
                    ++concurrentTerm;
                }

                // 构造投票请求
                RaftVoteReqWrapper.RaftVoteReq.Builder raftVoteReq
                        = RaftVoteReqWrapper.RaftVoteReq.newBuilder()
                        // 初始投票给自己
                        .setLeaderId(peerId)
                        .setTerm(concurrentTerm);
                log.debug("构建的选票 leader:{}, term:{}", raftVoteReq.getLeaderId(), raftVoteReq.getTerm());

                RaftReqWrapper.RaftReq raftReq
                        = RaftReqWrapper.RaftReq.newBuilder()
                        .setReqType(RaftReqWrapper.RaftReq.ReqType.VOTE)
                        .setVoteReq(raftVoteReq)
                        .build();
                log.debug("构建的选票请求raft对象:{}", raftReq);

                // 向所有的客户端发起选票请求
                futureList = new ArrayList<>();
                for (String peer : IocContainer.getInstance().getObj(PeerFinder.class).getPeers()) {
                    if (IocContainer.getInstance().getObj(PeerFinder.class).isSelf(peer)) {
                        continue;
                    }

                    Future<RaftResWrapper.RaftRes> raftResFuture = CALL_PEER_THREAD_POOL.submit(new CallPeerTask(raftReq, peer));
                    futureList.add(raftResFuture);
                }
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
                        if (raftRes != null && "".equals(raftRes.getErrorMsg())) {
                            log.debug("投票响应:{}, 状态:{}", raftRes, raftRes.getRaftVoteRes().getResult());
                            RaftVoteResWrapper.RaftVoteRes raftVoteRes = raftRes.getRaftVoteRes();
                            if (raftVoteRes.getResult().equals(RaftVoteResWrapper.RaftVoteRes.Result.ACCEPT)) {
                                voteNums++;
                            } else if (raftVoteRes.getResult().equals(RaftVoteResWrapper.RaftVoteRes.Result.TERM_EXPIRE)) {
                                // 检查是不是自己 重启的场景
                                if (raftVoteRes.getLeaderId() == peerId) {
                                    concurrentTerm = raftVoteRes.getTerm();
                                    becomeLeader();
                                    return;
                                }
                                synchronized (STATE_LOCK) {
                                    if (raftVoteRes.getTerm() > concurrentTerm) {
                                        log.debug("投票响应中, 包含大于自己的term:{}, leaderId:{}, 开始应用", raftVoteRes.getTerm(), raftVoteRes.getLeaderId());

                                        concurrentTerm = raftVoteRes.getTerm();
                                        leaderPeerId = raftVoteRes.getLeaderId();
                                        return;
                                    }
                                }
                            }
                            iterator.remove();
                        } else {
                            // 有异常
                            log.error("选票发送后收到的响应有异常:{}", raftRes);
                            // TODO: 2022/9/24 网络异常相关的容错设计 记录失败节点 重试
                            iterator.remove();
                        }
                    }
                }
            }
            log.debug("收到所有的投票结果, 一共票数:{}", voteNums);
            // 是否票数过半
            if (isQuorum(voteNums)) {
                becomeLeader();
                break;
            }

            if (!untilSuccess) {
                // 跳出投票
                break;
            }

            if (leaderPeerId != oldLeaderId) {
                log.debug("等待投票响应期间leader，term发生更改, 不需要发送选票了");
                return;
            }

            log.debug("继续投票....");
        }
    }

    /**
     * 收到选票请求
     */
    public RaftResWrapper.RaftRes receiveVote(RaftVoteReqWrapper.RaftVoteReq req) {
        log.debug("收到投票, leaderId:{}, term:{}", req.getLeaderId(), req.getTerm());
        synchronized (STATE_LOCK) {
            // 检查期数
            if (req.getTerm() > concurrentTerm) {
                log.debug("收到选票期数大于当前期数的选票, 选票请求:{}", req);
                // 当前节点期数过期了
                // 认可选票的leader
                concurrentTerm = req.getTerm();
                leaderPeerId = req.getLeaderId();
                becomeFollower();

                RaftVoteResWrapper.RaftVoteRes.Builder raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                        .setResult(RaftVoteResWrapper.RaftVoteRes.Result.ACCEPT);

                return RaftResWrapper.RaftRes.newBuilder()
                        .setRaftVoteRes(raftVoteRes)
                        .build();
            }
            if (req.getTerm() < concurrentTerm) {
                log.debug("收到过期选票:{}", req);
                // 过期的选票
                RaftVoteResWrapper.RaftVoteRes.Builder raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                        .setResult(RaftVoteResWrapper.RaftVoteRes.Result.TERM_EXPIRE)
                        .setLeaderId(leaderPeerId)
                        .setTerm(concurrentTerm);

                return RaftResWrapper.RaftRes.newBuilder()
                        .setRaftVoteRes(raftVoteRes)
                        .build();
            }
            // 同期选票
            // 如果当前节点没有投票 就把票投给对应的节点
            if (leaderPeerId == -1) {
                log.debug("未投票前收到选票:{}, 赞同选票", req);
                leaderPeerId = req.getLeaderId();
                becomeFollower();

                RaftVoteResWrapper.RaftVoteRes.Builder raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                        .setResult(RaftVoteResWrapper.RaftVoteRes.Result.ACCEPT);

                return RaftResWrapper.RaftRes.newBuilder()
                        .setRaftVoteRes(raftVoteRes)
                        .build();
            }
            log.debug("收到同期选票, 开始验证心跳...");
            // 检测心跳是否正常
            if (lastReceiveHeartbeatTime != 0L
                    && lastReceiveHeartbeatTime + MAX_MISSING_HEARTBEAT_COUNT * HEARTBEAT_INTERVAL_MS < System.currentTimeMillis()) {
                // 没有收到心跳太久
                log.debug("收到同期选票, 本节点认定leader心跳过期...");
                if (reElectFlag) {
                    log.debug("收到同期选票, 本节点认定leader心跳过期, 但已经投过票了... 拒绝选票:{}", req);
                    // 已经重新投了
                    RaftVoteResWrapper.RaftVoteRes.Builder raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                            .setResult(RaftVoteResWrapper.RaftVoteRes.Result.REJECT);

                    return RaftResWrapper.RaftRes.newBuilder()
                            .setRaftVoteRes(raftVoteRes)
                            .build();
                }
                // 认可选票
                log.debug("收到同期选票, 本节点认定leader心跳过期, 认可选票:{}", req);
                reElectFlag = true;
                leaderPeerId = req.getLeaderId();
                RaftVoteResWrapper.RaftVoteRes.Builder raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                        .setResult(RaftVoteResWrapper.RaftVoteRes.Result.ACCEPT);

                return RaftResWrapper.RaftRes.newBuilder()
                        .setRaftVoteRes(raftVoteRes)
                        .build();
            }
            if (req.getTerm() == concurrentTerm && req.getLeaderId() == leaderPeerId) {
                log.debug("收到同期选票, 收到的内容和本节一致, req:{}, 保证幂等同意选票", req);
                RaftVoteResWrapper.RaftVoteRes.Builder raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                        .setResult(RaftVoteResWrapper.RaftVoteRes.Result.ACCEPT);

                return RaftResWrapper.RaftRes.newBuilder()
                        .setRaftVoteRes(raftVoteRes)
                        .build();
            }

            log.debug("收到同期选票, leader心跳正常或未开始心跳, 拒绝选票:{}", req);
            // 已经投票了 拒绝选票
            RaftVoteResWrapper.RaftVoteRes.Builder raftVoteRes = RaftVoteResWrapper.RaftVoteRes.newBuilder()
                    .setResult(RaftVoteResWrapper.RaftVoteRes.Result.REJECT);

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
            log.debug("收到低于任期的心跳:{}, 提供最新的leaderId:{}, term:{}", req, leaderPeerId, concurrentTerm);
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
        log.debug("收到leader:{}, term:{}, 正常心跳, 重置心跳时间", req.getLeaderId(), req.getTerm());
        if (req.getLeaderId() != leaderPeerId) {
            log.error("脑裂... 本节点认定Leader:{}, 任期:{}, 收到req:{}", leaderPeerId, concurrentTerm, req);
            return RaftResWrapper.RaftRes.newBuilder()
                    .setRaftHeartbeatRes(
                            RaftHeartbeatResWrapper.RaftHeartbeatRes.newBuilder()
                                    .setResType(RaftHeartbeatResWrapper.RaftHeartbeatRes.ResponseType.OK)
                    )
                    .build();
        }
        // 正常反馈收到心跳 重新计时收到心跳的时间
        lastReceiveHeartbeatTime = System.currentTimeMillis();
        // 应用任期 leaderId
        leaderPeerId = req.getLeaderId();
        concurrentTerm = req.getTerm();
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
            if (state == RaftStateEnum.FOLLOWER) {
                log.debug("当前节点:{}, 从follower变成Leader", peerId);
                if (heartbeatCheckFuture != null) {
                    heartbeatCheckFuture.cancel(true);
                }
            }
            state = RaftStateEnum.LEADER;
            // Leader 心跳逻辑 检查数据逻辑
            // TODO: 2022/9/24 检查数据

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
                if (lastReceiveHeartbeatTime == 0L) {
                    return;
                }
                // 超过三次没有收到心跳
                if (lastReceiveHeartbeatTime + MAX_MISSING_HEARTBEAT_COUNT * HEARTBEAT_INTERVAL_MS < System.currentTimeMillis()) {
                    if (!reElectFlag) {
                        log.debug("节点:{}, 超过:{}ms, 没有收到:{}的心跳, 开始重新选举", peerId, (System.currentTimeMillis() - lastReceiveHeartbeatTime), leaderPeerId);
                        reElect();
                    }
                }
            }, 10000L, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
        }
    }

    private void reElect() {
        reElectFlag = true;
        log.debug("重新选举, 设置的LeaderId:{}", leaderPeerId);
        while (true) {
            // 发起选票
            sendVote(false, false);
            // 每轮投票都检测
            if (state == RaftStateEnum.LEADER) {
                reElectFlag = false;
                return;
            }
            if (leaderPeerId != peerId) {
                // 过程中投了别的节点
                log.debug("重新选举中,投票了其他节点:{}", leaderPeerId);
                return;
            }
            // 检测心跳是否恢复
            if (lastReceiveHeartbeatTime + MAX_MISSING_HEARTBEAT_COUNT * HEARTBEAT_INTERVAL_MS < System.currentTimeMillis()) {
                // 没有恢复
                continue;
            }
            // 恢复心跳了 leader恢复
            reElectFlag = false;
            return;
        }
    }

    private boolean isQuorum(int nums) {
        return nums > IocContainer.getInstance().getObj(PeerFinder.class).getPeers().size() / 2;
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
    private record CallPeerTask(MessageLite raftReq, String peer) implements Callable<RaftResWrapper.RaftRes> {

        /**
         * 异步请求
         *
         * @return raft 响应
         */
        @Override
        public RaftResWrapper.RaftRes call() {
            log.debug("发送的请求类型是:{}", raftReq.getClass().getName());
            try {
                MessageLite messageLite = NetworkClient.getInstance()
                        .sendReq(raftReq,
                                peer,
                                StorageConfig.RAFT_PORT,
                                RaftResWrapper.RaftRes.getDefaultInstance());
                log.debug("call peer task 收到响应:{}, 类型:{}", messageLite, messageLite.getClass().getName());

                return (RaftResWrapper.RaftRes) messageLite;
            } catch (Exception e) {
                log.error("call 其他节点出现网络异常:{}", raftReq, e);
                return RaftResWrapper.RaftRes.newBuilder()
                        .setErrorMsg(e.getMessage())
                        .build();
            }
        }

    }

    /**
     * 心跳任务
     */
    private class HeartbeatTask implements Runnable {

        @Override
        public void run() {
            log.debug("leader:{}, 开始执行心跳任务...", leaderPeerId);
            // 构造心跳
            RaftHeartbeatReqWrapper.RaftHeartbeatReq.Builder heartbeatReq = RaftHeartbeatReqWrapper.RaftHeartbeatReq.newBuilder()
                    .setMaxOffset(IocContainer.getInstance().getObj(PartitionMsgStorage.class).getGlobalIndexWrote())
                    .setLeaderId(leaderPeerId)
                    .setTerm(concurrentTerm);

            RaftReqWrapper.RaftReq req = RaftReqWrapper.RaftReq.newBuilder()
                    .setReqType(RaftReqWrapper.RaftReq.ReqType.HEARTBEAT)
                    .setHeartbeatReq(heartbeatReq)
                    .build();

            // 向所有的节点发送心跳
            for (String peer : IocContainer.getInstance().getObj(PeerFinder.class).getPeers()) {
                if (IocContainer.getInstance().getObj(PeerFinder.class).isSelf(peer)) {
                    continue;
                }

                Future<RaftResWrapper.RaftRes> raftResFuture = CALL_PEER_THREAD_POOL.submit(new CallPeerTask(req, peer));
                try {
                    RaftResWrapper.RaftRes raftRes = raftResFuture.get(1000L, TimeUnit.MILLISECONDS);
                    // 处理请求
                    if (raftRes != null) {
                        RaftHeartbeatResWrapper.RaftHeartbeatRes raftHeartbeatRes = raftRes.getRaftHeartbeatRes();
                        if (raftHeartbeatRes.getResType().equals(RaftHeartbeatResWrapper.RaftHeartbeatRes.ResponseType.TERM_EXPIRED)) {
                            synchronized (STATE_LOCK) {
                                // 当前Leader过期了
                                log.debug("收到心跳反馈leader任期过期, 当前leaderId:{}, term:{}, 心跳返回中的leader:{}, 任期:{}",
                                        leaderPeerId, concurrentTerm, raftHeartbeatRes.getLeaderId(), raftHeartbeatRes.getTerm());
                                leaderPeerId = raftHeartbeatRes.getLeaderId();
                                concurrentTerm = raftHeartbeatRes.getTerm();
                                becomeFollower();
                            }

                        }
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

}
