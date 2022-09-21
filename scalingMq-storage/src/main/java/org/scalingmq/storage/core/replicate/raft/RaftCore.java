package org.scalingmq.storage.core.replicate.raft;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.net.NetworkClient;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.replicate.raft.entity.RaftReqWrapper;
import org.scalingmq.storage.core.replicate.raft.entity.RaftResWrapper;
import org.scalingmq.storage.core.replicate.raft.entity.RaftVoteReqWrapper;
import org.scalingmq.storage.core.replicate.raft.entity.RaftVoteResWrapper;
import org.scalingmq.storage.lifecycle.Lifecycle;

import java.util.ArrayList;
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
     * 当前节点的角色 默认候选者
     */
    private RaftStateEnum state = RaftStateEnum.CANDIDATE;

    /**
     * 当前节点的期数
     */
    private final int concurrentTerm = 0;

    /**
     * 当前节点ID
     */
    private int peerId;

    /**
     * leader节点ID
     */
    private final int leaderPeerId = -1;

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
        if (storageConfig.getCoordinatorNums() != null && storageConfig.getCoordinatorNums().size() > 0) {
            if (storageConfig.getCoordinatorNums().contains(peerId)) {
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
        synchronized (STATE_LOCK) {
            if (!state.equals(RaftStateEnum.CANDIDATE)) {
                return;
            }
            // 发送选票
            sendVote();
        }
    }

    /**
     * 发送选票 解析结果
     */
    private void sendVote() {
        int sleepTime = RandomGeneratorFactory.getDefault().create(System.currentTimeMillis())
                .ints(1, MIN_VOTE_TIME_INTERVAL_MS, MAX_VOTE_TIME_INTERVAL_MS).toArray()[0];
        try {
            TimeUnit.MILLISECONDS.sleep(sleepTime);
        } catch (InterruptedException e) {
            // ignore
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

        // 继续投票
        doCandidate();
    }

    /**
     * 成为Leader
     */
    private void becomeLeader() {

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
         * @return raft 响应
         */
        @Override
        public RaftResWrapper.RaftRes call() {
           return  (RaftResWrapper.RaftRes) NetworkClient.getInstance()
                    .sendReq(raftReq,
                            peer,
                            StorageConfig.getInstance().getRaftPort(),
                            RaftVoteReqWrapper.RaftVoteReq.getDefaultInstance());
        }

    }

}
