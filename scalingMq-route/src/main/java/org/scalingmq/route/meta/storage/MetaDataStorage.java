package org.scalingmq.route.meta.storage;

import java.util.Map;

/**
 * 元数据存储接口
 * @author renyansong
 */
public interface MetaDataStorage {

    /**
     * 获取元数据
     * @param namespace 命名空间
     * @param name 配置名称
     * @return 配置数据
     */
    Map<String, String> getMetadata(String namespace, String name);

    /**
     * 存储元数据
     * @param data 数据
     * @param namespace 命名空间
     * @param name 配置名称
     * @return 操作结果
     */
    boolean storageMetadata(Map<String, String> data, String namespace, String name);

}
