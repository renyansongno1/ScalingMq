package org.scalingmq.route.meta;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.utils.PojoUtil;
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
        log.debug("开始创建topic元数据:{}, partition:{}", topicName, partitionNums);
        TopicMetadata topicMetadata = getTopicMetadata(topicName);
        if (topicMetadata == null) {
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
                return METADATA_STORAGE.storageMetadata(
                        PojoUtil.objectToMap(topicMetadata), RouteConfig.getInstance().getNamespace(), TopicMetadata.CONF_NAME_PREFIX + topicName);
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    /**
     * 查询topic的元数据
     * @param topicName topic的名称
     * @return topic的元数据
     */
    public TopicMetadata getTopicMetadata(String topicName) {
        log.debug("查询topic的metadata:{}", topicName);
        Lock lock = READ_WRITE_LOCK.readLock();
        lock.lock();
        try {
            Map<String, String> metadata
                    = METADATA_STORAGE.getMetadata(RouteConfig.getInstance().getNamespace(), TopicMetadata.CONF_NAME_PREFIX + topicName);
            if (metadata == null) {
                return null;
            }
            TopicMetadata topicMetadata = PojoUtil.mapToObject(metadata, TopicMetadata.class);
            if (topicMetadata == null) {
                return null;
            }
            log.debug("topic:{}, metadata:{}", topicName, topicMetadata.toString());
            return topicMetadata;
        } finally {
            lock.unlock();
        }
    }

}
