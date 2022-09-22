package org.scalingmq.kubernetes.api;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.ClientBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * k8s的client
 * @author renyansong
 */
@Slf4j
public class K8sApiClient {

    private static final K8sApiClient INSTANCE = new K8sApiClient();

    private static volatile boolean CLIENT_INIT = false;

    private static final CoreV1Api CORE_V1_API = new CoreV1Api();

    private K8sApiClient() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public static K8sApiClient getInstance() {
        if (!CLIENT_INIT) {
            synchronized (K8sApiClient.class) {
                if (!CLIENT_INIT) {
                    try {
                        // loading the in-cluster config, including:
                        //   1. service-account CA
                        //   2. service-account bearer-token
                        //   3. service-account namespace
                        //   4. master endpoints(ip, port) from pre-set environment variables
                        ApiClient client = ClientBuilder.cluster().build();

                        // if you prefer not to refresh service account token, please use:
                        // ApiClient client = ClientBuilder.oldCluster().build();
                        // set the global default api-client to the in-cluster one from above
                        Configuration.setDefaultApiClient(client);
                    } catch (IOException e) {
                        log.error("k8s client init error", e);
                        return null;
                    }
                    CLIENT_INIT = true;
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 创建config map
     * @param namespace configmap所属的namespace
     * @param configName 配置名称
     * @param data 配置数据
     * @return 创建结果
     */
    public boolean createConfigMap(String namespace, String configName, Map<String, String> data) {
        try {
            V1ConfigMap v1ConfigMap = new V1ConfigMap();
            v1ConfigMap.setData(data);
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setName(configName);
            v1ConfigMap.setMetadata(v1ObjectMeta);
            // 调用api
            CORE_V1_API.createNamespacedConfigMap(namespace, v1ConfigMap, Boolean.TRUE.toString(), null, null, null);
            return true;
        } catch (ApiException e) {
            log.error("创建configMap失败", e);
            return false;
        }
    }

    /**
     * 通过namespace 和 name
     * 查询config map
     * @param namespace 命名空间
     * @param name configmap的名称
     * @return 数据map
     */
    public Map<String, String> getConfigMapByNsAndName(String namespace, String name) {
        try {
            V1ConfigMap v1ConfigMap = CORE_V1_API.readNamespacedConfigMap(name, namespace, Boolean.TRUE.toString());
            if (v1ConfigMap == null) {
                return null;
            }
            return v1ConfigMap.getData();
        } catch (ApiException e) {
            log.error("查询:{}.{} configmap 异常", namespace, name, e);
            return null;
        }
    }

}
