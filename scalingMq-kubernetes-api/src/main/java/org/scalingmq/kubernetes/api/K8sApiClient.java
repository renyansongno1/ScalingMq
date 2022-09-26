package org.scalingmq.kubernetes.api;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.utils.StopWatch;

import java.io.IOException;
import java.util.*;

/**
 * k8s的client
 * @author renyansong
 */
@Slf4j
public class K8sApiClient {

    private static final K8sApiClient INSTANCE = new K8sApiClient();

    private static volatile boolean CLIENT_INIT = false;

    private static final CoreV1Api CORE_V1_API = new CoreV1Api();

    private static final AppsV1Api APPS_V_1_API = new AppsV1Api();

    private static final int NOT_FOUND = 404;

    private static final int ALREADY_EXIST = 409;

    private static ApiClient CLIENT = null;

    private K8sApiClient() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public static K8sApiClient getInstance() {
        StopWatch stopWatch = null;
        if (log.isDebugEnabled()) {
            stopWatch = new StopWatch("k8s 客户端初始化(k8s client init)");
            stopWatch.start("开始获取锁和判断初始化状态(k8s client init get lock)");
        }
        if (!CLIENT_INIT) {
            synchronized (INSTANCE) {
                if (!CLIENT_INIT) {
                    if (stopWatch != null) {
                        stopWatch.stop();
                    }
                    try {
                        // loading the in-cluster config, including:
                        //   1. service-account CA
                        //   2. service-account bearer-token
                        //   3. service-account namespace
                        //   4. master endpoints(ip, port) from pre-set environment variables
                        if (stopWatch != null) {
                            stopWatch.start("k8s client build");
                        }
                        CLIENT = ClientBuilder.cluster().build();

                        if (stopWatch != null) {
                            stopWatch.stop();
                        }

                        // if you prefer not to refresh service account token, please use:
                        // ApiClient client = ClientBuilder.oldCluster().build();
                        // set the global default api-client to the in-cluster one from above
                        if (stopWatch != null) {
                            stopWatch.start("set api client");
                        }
                        Configuration.setDefaultApiClient(CLIENT);
                        CORE_V1_API.setApiClient(CLIENT);
                        APPS_V_1_API.setApiClient(CLIENT);

                        if (stopWatch != null) {
                            stopWatch.stop();
                            log.debug(stopWatch.prettyPrint());
                        }
                        CLIENT_INIT = true;
                        return INSTANCE;
                    } catch (IOException e) {
                        if (stopWatch != null) {
                            if (stopWatch.isRunning()) {
                                stopWatch.stop();
                            }
                            log.debug(stopWatch.prettyPrint());
                        }
                        log.error("k8s client init error", e);
                        return null;
                    }
                }
            }
        }
        if (stopWatch != null) {
            stopWatch.stop();
            log.debug(stopWatch.prettyPrint());
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
            log.error("创建configMap失败, api exception, msg:{}", e.getResponseBody(), e);
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
            log.debug("k8s client 查询configmap, ns:{}, name:{}", namespace, name);
            V1ConfigMap v1ConfigMap = CORE_V1_API.readNamespacedConfigMap(name, namespace, Boolean.TRUE.toString());
            if (v1ConfigMap == null) {
                return null;
            }
            return v1ConfigMap.getData();
        } catch (ApiException e) {
            if (e.getCode() == NOT_FOUND) {
                return null;
            }
            log.error("查询:{}.{} configmap 异常信息:{}", namespace, name, e.getResponseBody(), e);
            return null;
        }
    }

    /**
     * 创建pod
     */
    @SuppressWarnings("AlibabaMethodTooLong")
    public boolean createPods(String namespace,
                              String podName,
                              Map<String, String> podMetadataLabelMap,
                              Integer replicas,
                              String serviceName,
                              Map<String, String> specLabelMap,
                              String templateMetadataName,
                              Map<String, String> templateMetadataLabelMap,
                              String containerName,
                              String imageName,
                              List<Integer> containerPorts,
                              List<String> containerNames,
                              Map<String, String> envMap,
                              String cpuResource,
                              String memoryResource) {
        try {
            V1StatefulSet statefulSet = new V1StatefulSet();
            // header
            statefulSet.setApiVersion("apps/v1");
            statefulSet.setKind("StatefulSet");

            // metadata
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setLabels(podMetadataLabelMap);
            v1ObjectMeta.setName(podName);
            v1ObjectMeta.setNamespace(namespace);
            statefulSet.setMetadata(v1ObjectMeta);

            // spec
            V1StatefulSetSpec v1StatefulSetSpec = new V1StatefulSetSpec();
            v1StatefulSetSpec.setReplicas(replicas);
            // selector
            V1LabelSelector v1LabelSelector = new V1LabelSelector();
            v1LabelSelector.setMatchLabels(specLabelMap);
            v1StatefulSetSpec.setSelector(v1LabelSelector);

            v1StatefulSetSpec.setServiceName(serviceName);
            // template
            V1PodTemplateSpec v1PodTemplateSpec = new V1PodTemplateSpec();
            V1ObjectMeta templateMetadata = new V1ObjectMeta();
            templateMetadata.setName(templateMetadataName);
            templateMetadata.setLabels(templateMetadataLabelMap);
            v1PodTemplateSpec.setMetadata(templateMetadata);

            V1PodSpec v1PodSpec = new V1PodSpec();
            V1Container v1Container = new V1Container();
            v1Container.setName(containerName);
            v1Container.setImage(imageName);
            v1Container.setImagePullPolicy("IfNotPresent");

            List<V1ContainerPort> ports = new ArrayList<>();
            for (int i = 0; i < containerPorts.size(); i++) {
                Integer containerPort = containerPorts.get(i);
                String containerPortName = containerNames.get(i);
                V1ContainerPort v1ContainerPort = new V1ContainerPort();
                v1ContainerPort.setContainerPort(containerPort);
                v1ContainerPort.setName(containerPortName);
                ports.add(v1ContainerPort);
            }

            v1Container.setPorts(ports);

            List<V1EnvVar> envVarList = new ArrayList<>();
            for (Map.Entry<String, String> stringStringEntry : envMap.entrySet()) {
                V1EnvVar v1EnvVar = new V1EnvVar();
                v1EnvVar.setName(stringStringEntry.getKey());
                v1EnvVar.setValue(stringStringEntry.getValue());
                envVarList.add(v1EnvVar);
            }
            // 补充默认env
            V1EnvVar v1EnvVar = new V1EnvVar();
            v1EnvVar.setName("POD_NAMESPACE");
            V1EnvVarSource v1EnvVarSource = new V1EnvVarSource();
            V1ObjectFieldSelector v1ObjectFieldSelector = new V1ObjectFieldSelector();
            v1ObjectFieldSelector.setFieldPath("metadata.namespace");
            v1ObjectFieldSelector.setApiVersion("v1");
            v1EnvVarSource.setFieldRef(v1ObjectFieldSelector);
            v1EnvVar.setValueFrom(v1EnvVarSource);
            envVarList.add(v1EnvVar);
            v1Container.setEnv(envVarList);

            V1ResourceRequirements v1ResourceRequirements = new V1ResourceRequirements();
            Map<String, Quantity> resourceMap = new HashMap<>();
            resourceMap.put("cpu", Quantity.fromString(cpuResource));
            resourceMap.put("memory", Quantity.fromString(memoryResource));
            v1ResourceRequirements.setLimits(resourceMap);
            v1ResourceRequirements.setRequests(resourceMap);

            v1Container.setResources(v1ResourceRequirements);

            v1PodSpec.setContainers(
                    Collections.singletonList(
                            v1Container
                    )
            );
            v1PodSpec.setRestartPolicy("Always");
            v1PodTemplateSpec.setSpec(v1PodSpec);

            v1StatefulSetSpec.setTemplate(v1PodTemplateSpec);

            statefulSet.setSpec(v1StatefulSetSpec);
            APPS_V_1_API.createNamespacedStatefulSet(namespace, statefulSet, Boolean.TRUE.toString(), null, null, null);
            return true;
        } catch (ApiException e) {
            if (e.getCode() == ALREADY_EXIST) {
                return true;
            }
            log.error("创建:{}.{} pod 异常信息:{}", namespace, podName, e.getResponseBody(), e);
            return false;
        }
    }

    /**
     * 创建service
     */
    public boolean createService(String namespace,
                                 String name,
                                 Map<String, String> specSelectorMap,
                                 List<Integer> portList,
                                 List<String> portNameList,
                                 String clusterIp) {
        try {
            V1Service v1Service = new V1Service();
            v1Service.setApiVersion("v1");
            v1Service.setKind("Service");
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setName(name);
            v1ObjectMeta.setNamespace(namespace);
            v1Service.setMetadata(v1ObjectMeta);

            V1ServiceSpec v1ServiceSpec = new V1ServiceSpec();
            v1ServiceSpec.setSelector(specSelectorMap);

            List<V1ServicePort> ports = new ArrayList<>();
            for (int i = 0; i < portList.size(); i++) {
                Integer port = portList.get(i);
                String portName = portNameList.get(i);
                V1ServicePort v1ServicePort = new V1ServicePort();
                v1ServicePort.setPort(port);
                v1ServicePort.setName(portName);
                ports.add(v1ServicePort);
            }

            v1ServiceSpec.setPorts(ports);
            v1ServiceSpec.setClusterIP(clusterIp);

            v1Service.setSpec(v1ServiceSpec);
            CORE_V1_API.createNamespacedService(namespace, v1Service, Boolean.TRUE.toString(), null, null, null);
            return true;
        } catch (ApiException e) {
            if (e.getCode() == ALREADY_EXIST) {
                return true;
            }
            log.error("创建:{}.{} service 异常信息:{}", namespace, name, e.getResponseBody(), e);
            return false;
        }
    }

}
