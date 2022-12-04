package org.scalingmq.storage.core.storage;

import java.util.TreeMap;

/**
 * 多级存储的映射关系
 * @author renyansong
 */
public class StorageMapping {

    /**
     * k -> 存储优先级
     * v -> 存储实现类
     */
    private static final TreeMap<Integer, StorageClass> STORAGE_CLASS_MAP = new TreeMap<>();

    /**
     * 添加存储实现
     * @param storagePriority 存储优先级
     * @param storageClass 存储实现
     */
    public static void addStorageClass(int storagePriority, StorageClass storageClass) {
        synchronized (StorageMapping.class) {
            STORAGE_CLASS_MAP.put(storagePriority, storageClass);
        }
    }

    public static TreeMap<Integer, StorageClass> getMapping() {
        return STORAGE_CLASS_MAP;
    }

}
