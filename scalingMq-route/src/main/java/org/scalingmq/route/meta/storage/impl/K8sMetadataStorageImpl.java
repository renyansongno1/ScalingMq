package org.scalingmq.route.meta.storage.impl;

import org.scalingmq.kubernetes.api.K8sApiClient;
import org.scalingmq.route.meta.storage.MetaDataStorage;

import java.util.Map;

/**
 * k8s实现的元数据存储
 * @author renyansong
 */
public class K8sMetadataStorageImpl implements MetaDataStorage {

    /**
     * 调用k8s api configmap的方式获取元数据
     * @param namespace 命名空间
     * @param name 配置名称
     * @return 元数据map
     */
    @Override
    public Map<String, String> getMetadata(String namespace, String name) {
        K8sApiClient k8sApiClient = K8sApiClient.getInstance();
        if (k8sApiClient == null) {
            return null;
        }
        return k8sApiClient.getConfigMapByNsAndName(namespace, name);
    }

    /**
     * 调用k8s api configmap的方式存储元数据
     * @param data 数据
     * @param namespace 命名空间
     * @param name 配置名称
     * @return 操作结果
     */
    @Override
    public boolean storageMetadata(Map<String, String> data, String namespace, String name) {
        K8sApiClient k8sApiClient = K8sApiClient.getInstance();
        if (k8sApiClient == null) {
            return false;
        }
        return k8sApiClient.createConfigMap(namespace, name, data);
    }

}
