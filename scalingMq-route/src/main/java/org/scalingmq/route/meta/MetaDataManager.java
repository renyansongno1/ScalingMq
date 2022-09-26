package org.scalingmq.route.meta;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.utils.PojoUtil;
import org.scalingmq.common.utils.StopWatch;
import org.scalingmq.route.conf.RouteConfig;
import org.scalingmq.route.meta.schema.TopicMetadata;
import org.scalingmq.route.meta.storage.MetaDataStorage;
import org.scalingmq.route.meta.storage.impl.K8sMetadataStorageImpl;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 元数据管理
 * @author renyansong
 */
@Slf4j
public class MetaDataManager {

    private static final MetaDataManager INSTANCE = new MetaDataManager();

    private static final MetaDataStorage METADATA_STORAGE = new K8sMetadataStorageImpl();

    private static final ReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();

    private MetaDataManager() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static MetaDataManager getInstance() {
        return INSTANCE;
    }

    /**
     * 创建topic的元数据
     * @param topicName topic的名称
     * @param partitionNums 分区数量
     * @return 操作结果
     */
    public boolean createTopicMetadata(String topicName, Integer partitionNums) {
        if (log.isDebugEnabled()) {
            log.debug("开始创建topic元数据:{}, partition:{}", topicName, partitionNums);
        }
        StopWatch stopWatch = new StopWatch("创建topic元数据(create topic metadata)");
        stopWatch.start("查询topic元数据(query topic metadata)");
        TopicMetadata topicMetadata = getTopicMetadata(topicName);
        stopWatch.stop();
        if (topicMetadata == null) {
            stopWatch.start("开始创建topic元数据(start create topic metadata)");
            // 没有创建过
            Lock lock = READ_WRITE_LOCK.writeLock();
            lock.lock();
            try {
                topicMetadata = getTopicMetadata(topicName);
                if (topicMetadata != null) {
                    return true;
                }
                topicMetadata = TopicMetadata.builder()
                        .partitionNums(partitionNums)
                        .topicName(topicName)
                        .build();
                boolean storageMetadata = METADATA_STORAGE.storageMetadata(
                        PojoUtil.objectToMap(topicMetadata), RouteConfig.getInstance().getNamespace(), TopicMetadata.CONF_NAME_PREFIX + topicName);
                stopWatch.stop();
                log.debug(stopWatch.prettyPrint());
                return storageMetadata;
            } finally {
                lock.unlock();
            }
        }
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        log.debug(stopWatch.prettyPrint());
        return false;
    }

    /**
     * 查询topic的元数据
     * @param topicName topic的名称
     * @return topic的元数据
     */
    public TopicMetadata getTopicMetadata(String topicName) {
        StopWatch stopWatch = null;
        if (log.isDebugEnabled()) {
            log.debug("查询topic的metadata:{}", topicName);
            stopWatch = new StopWatch("获取topic元数据(get topic metadata)");
        }
        Lock lock = READ_WRITE_LOCK.readLock();
        lock.lock();
        try {
            if (stopWatch != null) {
                stopWatch.start("调用元数据存储组件查询元数据(storage component call)");
            }
            Map<String, String> metadata
                    = METADATA_STORAGE.getMetadata(RouteConfig.getInstance().getNamespace(), TopicMetadata.CONF_NAME_PREFIX + topicName);
            if (stopWatch != null) {
                stopWatch.stop();
            }
            if (metadata == null) {
                if (stopWatch != null) {
                    log.debug(stopWatch.prettyPrint());
                }
                return null;
            }
            if (stopWatch != null) {
                stopWatch.start("元数据对象从map转化为pojo(metadata map to pojo)");
            }
            TopicMetadata topicMetadata = PojoUtil.mapToObject(metadata, TopicMetadata.class);
            if (stopWatch != null) {
                stopWatch.stop();
            }
            if (topicMetadata == null) {
                if (stopWatch != null) {
                    log.debug(stopWatch.prettyPrint());
                }
                return null;
            }
            log.debug("topic:{}, metadata:{}", topicName, topicMetadata);
            if (stopWatch != null) {
                log.debug(stopWatch.prettyPrint());
            }
            return topicMetadata;
        } finally {
            lock.unlock();
        }
    }

}
