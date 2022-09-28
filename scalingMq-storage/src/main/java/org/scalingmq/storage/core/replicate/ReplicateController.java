package org.scalingmq.storage.core.replicate;

import org.scalingmq.common.utils.Tuple;
import org.scalingmq.storage.core.replicate.entity.FollowerOffsetProgressReport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 复制控制器
 * @author renyansong
 */
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

    }

}
