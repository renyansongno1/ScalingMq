package org.scalingmq.route.manager;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.kubernetes.api.K8sApiClient;
import org.scalingmq.route.conf.RouteConfig;
import org.scalingmq.route.manager.template.StoragePodTemplate;
import org.scalingmq.route.manager.template.StorageServiceTemplate;
import org.scalingmq.route.meta.MetaDataManager;
import org.scalingmq.route.meta.schema.TopicMetadata;

/**
 * 路由管理
 *
 * @author renyansong
 */
@Slf4j
public class RouteManager {

    private static final RouteManager INSTANCE = new RouteManager();

    private RouteManager() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static RouteManager getInstance() {
        return INSTANCE;
    }

    /**
     * 调度存储pod
     *
     * @param topicName topic名
     */
    public boolean scheduleStoragePods(String topicName) {
        log.debug("开始为:{}, 调用存储pod", topicName);
        // 查询topic元数据
        TopicMetadata topicMetadata = MetaDataManager.getInstance().getTopicMetadata(topicName);
        if (topicMetadata == null) {
            log.debug("topic元数据为空, 跳过调度pods");
            return false;
        }
        // 调出创建存储节点的pod
        boolean schedRes = scheduleStoragePods0(topicName, topicMetadata.getPartitionNums());
        if (schedRes) {
            // TODO: 2022/9/23 更新元数据
        }
        return schedRes;
    }

    private boolean scheduleStoragePods0(String topicName, Integer partitions) {
        K8sApiClient instance = K8sApiClient.getInstance();
        if (instance == null) {
            return false;
        }

        // 创建service
        StorageServiceTemplate storageServiceTemplate = new StorageServiceTemplate(topicName);

        boolean srvRes = instance.createService(
                RouteConfig.getInstance().getNamespace(),
                storageServiceTemplate.getStorageServiceName(),
                StorageServiceTemplate.SPEC_SELECTOR_MAP,
                storageServiceTemplate.getPort(),
                storageServiceTemplate.getClusterIp()
        );
        if (!srvRes) {
            return false;
        }

        StoragePodTemplate storagePodTemplate = new StoragePodTemplate(storageServiceTemplate);
        storagePodTemplate.setReplicas(partitions);
        return instance.createPods(
                RouteConfig.getInstance().getNamespace(),
                storagePodTemplate.getPodName(),
                StoragePodTemplate.META_LABEL_MAP,
                storagePodTemplate.getReplicas(),
                storagePodTemplate.getServiceName(),
                StoragePodTemplate.SPEC_LABEL_MAP,
                storagePodTemplate.getTemplateMetadataName(),
                StoragePodTemplate.TEMP_LABEL_MAP,
                storagePodTemplate.getContainerName(),
                storagePodTemplate.getImageName(),
                storagePodTemplate.getContainerPort(),
                StoragePodTemplate.ENV_MAP,
                storagePodTemplate.getCpuResource(),
                storagePodTemplate.getMemoryResource()
        );
    }

}
