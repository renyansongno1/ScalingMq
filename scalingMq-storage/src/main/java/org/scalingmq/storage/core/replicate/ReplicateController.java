package org.scalingmq.storage.core.replicate;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.ioc.IocContainer;
import org.scalingmq.common.net.NetworkClient;
import org.scalingmq.common.utils.Tuple;
import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.storage.api.StorageApiResWrapper;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.replicate.entity.FollowerOffsetProgressReport;
import org.scalingmq.storage.core.replicate.raft.RaftCore;
import org.scalingmq.storage.core.storage.PartitionMsgStorage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 复制控制器
 * @author renyansong
 */
@Slf4j
public class ReplicateController {

    /**
     * leader part
     */
    public static class LeaderController {

        private static final ReadWriteLock FOLLOWER_OFFSET_RW_LOCK = new ReentrantReadWriteLock();

        /**
         * follower的复制进度 时间记录
         * map key -> follower hostname
         *     value -> tuple ->
         *                tuple one long -> 复制offset
         *                tuple two long -> timestamp
         */
        private static final Map<String, Tuple.TwoTuple<Long, Long>> FOLLOWER_PROGRESS_MAP = new ConcurrentHashMap<>();

        /**
         * 存储从节点的复制进度
         * @param followerOffsetProgressReport 上报从节点的复制情况
         */
        public static void updateFollowerOffset(FollowerOffsetProgressReport followerOffsetProgressReport) {
            Lock lock = FOLLOWER_OFFSET_RW_LOCK.writeLock();
            lock.lock();
            try {
                Tuple.TwoTuple<Long, Long> tuple = FOLLOWER_PROGRESS_MAP.get(followerOffsetProgressReport.getFollowerHostname());
                if (tuple == null) {
                    FOLLOWER_PROGRESS_MAP.put(followerOffsetProgressReport.getFollowerHostname(),
                            // 当前复制完成后的offset
                            Tuple.tuple(followerOffsetProgressReport.getLastFetchOffset(),
                                    // 时间戳
                                    System.currentTimeMillis()));
                    return;
                }
                tuple.update(followerOffsetProgressReport.getLastFetchOffset(), System.currentTimeMillis());
            } finally {
                lock.unlock();
            }
        }

    }

    /**
     * follower part
     */
    public static class FollowerController {

        private static volatile long leaderMaxOffset = -1L;

        private static final ScheduledThreadPoolExecutor FETCH_LEADER_MSG_THREAD_POOL =
                new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "follower-fetch-leader-sched-thread"));

        static {
            FETCH_LEADER_MSG_THREAD_POOL.scheduleWithFixedDelay(() -> {
                while (true) {
                    long nowLeaderMaxOffset = leaderMaxOffset;
                    if (nowLeaderMaxOffset != -1) {
                        // 拉取消息
                        PartitionMsgStorage partitionMsgStorage = IocContainer.getInstance().getObj(PartitionMsgStorage.class);
                        long globalIndexWrote = partitionMsgStorage.getGlobalIndexWrote();
                        if (globalIndexWrote >= nowLeaderMaxOffset) {
                            return;
                        }
                        // 发送网络请求拉取消息
                        StorageApiReqWrapper.StorageApiReq.FetchMsgReq req
                                = StorageApiReqWrapper.StorageApiReq.FetchMsgReq.newBuilder()
                                .setFollowerHostname(StorageConfig.getInstance().getHostname())
                                .setOffset(globalIndexWrote)
                                .build();
                        RaftCore raftCore = IocContainer.getInstance().getObj(RaftCore.class);
                        StorageApiResWrapper.StorageApiRes res = (StorageApiResWrapper.StorageApiRes) NetworkClient.getInstance()
                                .sendReq(req,
                                        raftCore.getLeaderAddr(),
                                        StorageConfig.RAFT_PORT,
                                        StorageApiResWrapper.StorageApiRes.getDefaultInstance());
                        if (!"".equals(res.getErrorCode())) {
                            log.error("拉取消息异常: code:{}, msg:{}", res.getErrorCode(), res.getErrorMsg());
                            continue;
                        }

                        StorageApiResWrapper.FetchMsgRes fetchMsgRes = res.getFetchMsgRes();
                        if (fetchMsgRes.getAlreadyLastOffset()) {
                            if (log.isDebugEnabled()) {
                                log.debug("拉取消息没有新的消息:{}", fetchMsgRes);
                            }
                            return;
                        }
                        // 存储到本地
                        List<ByteString> dataList = fetchMsgRes.getDataList();
                        for (ByteString bytes : dataList) {
                            partitionMsgStorage.append(bytes.toByteArray());
                        }
                        // 继续拉取
                        continue;
                    }
                    return;
                }
            }, 1000L, 500L, TimeUnit.MILLISECONDS);
        }

        /**
         * 应用leader的offset
         * @param leaderOffset leader的offset
         */
        public static void acceptLeaderOffset(long leaderOffset) {
            leaderMaxOffset = leaderOffset;
        }

    }

}
