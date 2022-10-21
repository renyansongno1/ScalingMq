package org.scalingmq.storage.core.replicate;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.ioc.IocContainer;
import org.scalingmq.common.net.NetworkClient;
import org.scalingmq.common.utils.Tuple;
import org.scalingmq.route.client.RouteAppClient;
import org.scalingmq.route.client.conf.RouteClientConfig;
import org.scalingmq.route.client.entity.IsrUpdateReqWrapper;
import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.storage.api.StorageApiResWrapper;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.replicate.entity.FollowerOffsetProgressReport;
import org.scalingmq.storage.core.replicate.raft.PeerFinder;
import org.scalingmq.storage.core.replicate.raft.RaftCore;
import org.scalingmq.storage.core.storage.PartitionMsgStorage;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;
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
        private static final Map<String, Tuple.TwoTuple<Long, Long>> FOLLOWER_PROGRESS_MAP = new HashMap<>();

        private static final ScheduledThreadPoolExecutor ISR_REPORT_SCHEDULE_POOL
                = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "leader-check-isr-task-thread"));

        /**
         * isr列表
         */
        private static final Set<String> ISR_ADDR = new CopyOnWriteArraySet<>();

        /**
         * 元数据节点通信客户端
         */
        private static final RouteAppClient ROUTE_APP_CLIENT = RouteAppClient.getInstance();

        static {
            RouteAppClient.getInstance().initConfig(
                    RouteClientConfig.builder()
                            .serverAddr(StorageConfig.getInstance().getRouteServerAddr())
                            .serverPort(Integer.valueOf(StorageConfig.getInstance().getRouteServerPort()))
                            .threadCount(StorageConfig.getInstance().getRouteClientThreadCount())
                            .build());
        }

        private static boolean SCHEDULED = false;

        /**
         * term
         */
        private static int TERM = 0;

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
                    FOLLOWER_PROGRESS_MAP.put(IocContainer.getInstance().getObj(PeerFinder.class)
                                    .returnPeerFullPath(followerOffsetProgressReport.getFollowerHostname()),
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

        /**
         * 定时检查isr的情况
         */
        public static void scheduleIsr(int term) {
            synchronized (ReplicateController.class) {
                if (SCHEDULED && TERM == term) {
                    return;
                }
                SCHEDULED = true;
                TERM = term;
                // 添加上自己
                ISR_ADDR.add(IocContainer.getInstance().getObj(PeerFinder.class)
                        .returnPeerFullPath(StorageConfig.getInstance().getHostname()));
            }
            if (log.isDebugEnabled()) {
                log.debug("开始更新isr");
            }
            ISR_REPORT_SCHEDULE_POOL.scheduleWithFixedDelay(() -> {
                Lock lock = FOLLOWER_OFFSET_RW_LOCK.readLock();
                lock.lock();
                try {
                    for (Map.Entry<String, Tuple.TwoTuple<Long, Long>> hostnameOffsetEntry : FOLLOWER_PROGRESS_MAP.entrySet()) {
                        Tuple.TwoTuple<Long, Long> offsetAndTimestamp = hostnameOffsetEntry.getValue();
                        if (offsetAndTimestamp.second + StorageConfig.getInstance().getMaxPartitionBackwardTime() < System.currentTimeMillis()) {
                            // 移除这个node
                            if (log.isDebugEnabled()) {
                                log.debug("isr列表中节点:{}拉取时间超过最大允许时间:{}ms, 最后拉取时间:{}, offset:{}",
                                        hostnameOffsetEntry.getKey(),
                                        StorageConfig.getInstance().getMaxPartitionBackwardTime(),
                                        offsetAndTimestamp.second,
                                        offsetAndTimestamp.first);
                            }
                            ISR_ADDR.remove(hostnameOffsetEntry.getKey());
                            continue;
                        }
                        boolean add = ISR_ADDR.add(hostnameOffsetEntry.getKey());
                        if (add && log.isDebugEnabled()) {
                            log.debug("isr列表新增加节点:{}, 最后拉取时间:{}, offset:{}",
                                    hostnameOffsetEntry.getKey(), offsetAndTimestamp.second, offsetAndTimestamp.first);
                        }
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("更新isr 开始上报ISR:{}", Arrays.toString(ISR_ADDR.toArray()));
                    }
                    // 上报ISR列表
                    IsrUpdateReqWrapper.IsrUpdateReq isrUpdateReq = IsrUpdateReqWrapper.IsrUpdateReq.newBuilder()
                            .setTopicName(StorageConfig.getInstance().getTopicName())
                            .setPartitionNum(Integer.parseInt(StorageConfig.getInstance().getPartitionNum()))
                            .addAllIsrAddrs(ISR_ADDR.stream().map(
                                    addr -> addr + ":" + StorageConfig.MSG_PORT
                            ).toList())
                            .setTerm(TERM)
                            .build();
                    try {
                        ROUTE_APP_CLIENT.reportIsrData(isrUpdateReq);
                    } catch (Exception e) {
                        log.error("上报元数据异常", e);
                    }
                } finally {
                    lock.unlock();
                }
            }, 10L, 3L, TimeUnit.SECONDS);
        }

        /**
         * 检查ISR的复制进度
         * @param offset 检查点
         * @return 是否都复制超过
         */
        public static boolean checkIsrOffset(long offset) {
            Lock lock = FOLLOWER_OFFSET_RW_LOCK.readLock();
            lock.lock();
            try {
                int count = ISR_ADDR.size();
                int replicatedCount = 0;
                for (String isr : ISR_ADDR) {
                    Tuple.TwoTuple<Long, Long> offsetAndTimestamp = FOLLOWER_PROGRESS_MAP.get(isr);
                    if (offsetAndTimestamp != null) {
                        if (offsetAndTimestamp.first >= offset) {
                            replicatedCount++;
                        }
                    }
                }
                return (replicatedCount >= count);
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

        private static final ScheduledFuture<?> SCHEDULED_FUTURE;

        static {
            SCHEDULED_FUTURE = FETCH_LEADER_MSG_THREAD_POOL.scheduleWithFixedDelay(() -> {
                if (log.isDebugEnabled()) {
                    log.debug("从节点开始拉取消息...");
                }
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
                        if (log.isDebugEnabled()) {
                            log.debug("开始向Leader:{}, 发送拉取消息请求:{}", raftCore.getLeaderAddr(), req);
                        }
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

        /**
         * 停止拉取任务 一般是follower变leader
         */
        public static void stopFetch() {
            SCHEDULED_FUTURE.cancel(true);
        }

    }

}
