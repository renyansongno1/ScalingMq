package org.scalingmq.storage.core.replicate.raft;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.net.NetworkClient;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.replicate.raft.entity.RaftVoteReqWrapper;
import org.scalingmq.storage.lifecycle.Lifecycle;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGeneratorFactory;

/**
 * raft算法实现
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
     * 当前节点的角色 默认候选者
     */
    private RaftStateEnum state = RaftStateEnum.CANDIDATE;

    /**
     * 当前节点的期数
     */
    private int concurrentTerm = 0;

    /**
     * 当前节点ID
     */
    private int peerId;

    /**
     * leader节点ID
     */
    private int leaderPeerId = -1;

    public static RaftCore getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("AlibabaSwitchStatement")
    private void init() {
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

        // 向所有的客户端发起选票请求
        for (String peer : PeerFinder.getInstance().getPeers()) {
            if (PeerFinder.getInstance().isSelf(peer)) {
                continue;
            }
            RaftVoteReqWrapper.RaftVoteReq res = (RaftVoteReqWrapper.RaftVoteReq) NetworkClient.getInstance()
                    .sendReq(raftVoteReq,
                            peer,
                            StorageConfig.getInstance().getRaftPort(),
                            RaftVoteReqWrapper.RaftVoteReq.getDefaultInstance());
            // 处理响应
        }

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

}
