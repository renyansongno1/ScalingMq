package org.scalingmq.storage.core;

import org.scalingmq.storage.core.cons.StorageAppendResult;

import java.util.Map;
import java.util.TreeMap;

/**
 * 分区消息存储实现
 * @author renyansong
 */
public class PartitionMsgStorage {

    private static final PartitionMsgStorage INSTANCE = new PartitionMsgStorage();

    /**
     * k -> 存储优先级
     * v -> 存储实现类
     */
    private static final Map<Integer, StorageClass> STORAGE_CLASS_MAP = new TreeMap<>();

    private PartitionMsgStorage() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static PartitionMsgStorage getInstance() {
        return INSTANCE;
    }

    /**
     * 添加存储实现
     * @param storagePriority 存储优先级
     * @param storageClass 存储实现
     */
    public void addStorageClass(int storagePriority, StorageClass storageClass) {
        synchronized (this) {
            STORAGE_CLASS_MAP.put(storagePriority, storageClass);
        }
    }

    /**
     * 追加消息到存储组件中
     * @param msgBody 消息数据
     * @return 消息物理偏移量
     */
    public long append(byte[] msgBody) {
        for (Map.Entry<Integer, StorageClass> storageClassEntry : STORAGE_CLASS_MAP.entrySet()) {
            StorageAppendResult appendResult = storageClassEntry.getValue().append(msgBody);
            if (!appendResult.getSuccess()) {
                continue;
            }
            // index

            // replicate

            // 成功
            return 0L;
        }
        return 0L;
    }

}
