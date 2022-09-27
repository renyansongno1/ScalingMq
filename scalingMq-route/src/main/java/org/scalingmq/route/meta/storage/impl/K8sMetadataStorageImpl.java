package org.scalingmq.route.meta.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.utils.StopWatch;
import org.scalingmq.kubernetes.api.K8sApiClient;
import org.scalingmq.route.meta.storage.MetaDataStorage;

import java.util.Map;

/**
 * k8s实现的元数据存储
 * @author renyansong
 */
@Slf4j
public class K8sMetadataStorageImpl implements MetaDataStorage {

    /**
     * 调用k8s api configmap的方式获取元数据
     * @param namespace 命名空间
     * @param name 配置名称
     * @return 元数据map
     */
    @Override
    public Map<String, String> getMetadata(String namespace, String name) {
        StopWatch stopWatch = null;
        if (log.isDebugEnabled()) {
            stopWatch = new StopWatch("k8s元数据存储实现获取元数据(k8s metadata impl get metadata)");
        }
        if (stopWatch != null) {
            stopWatch.start("获取k8s client客户端(get k8s client instance)");
        }
        K8sApiClient k8sApiClient = K8sApiClient.getInstance();
        if (stopWatch != null) {
            stopWatch.stop();
        }
        if (k8sApiClient == null) {
            if (stopWatch != null) {
                log.debug(stopWatch.prettyPrint());
            }
            return null;
        }
        if (stopWatch != null) {
            stopWatch.start("调用configmap api获取元数据(get metadata from configmap by api)");
        }
        Map<String, String> metadata = k8sApiClient.getConfigMapByNsAndName(namespace, name);
        if (stopWatch != null) {
            stopWatch.stop();
            log.debug(stopWatch.prettyPrint());
        }
        return metadata;
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

    /**
     * 调用k8s的client api实现patch configmap
     * @param namespace 命名空间
     * @param name 名称
     * @param patchJson patch的json
     * @return 操作结果
     */
    @Override
    public boolean patchMetadata(String namespace, String name, String patchJson) {
        K8sApiClient k8sApiClient = K8sApiClient.getInstance();
        if (k8sApiClient == null) {
            return false;
        }
        return k8sApiClient.updateConfigMap(namespace, name, patchJson);
    }
}
