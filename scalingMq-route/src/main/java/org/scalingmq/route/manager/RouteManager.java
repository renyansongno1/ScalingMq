package org.scalingmq.route.manager;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.kubernetes.api.K8sApiClient;
import org.scalingmq.route.conf.RouteConfig;
import org.scalingmq.route.manager.template.StoragePodTemplate;
import org.scalingmq.route.manager.template.StorageServiceTemplate;
import org.scalingmq.route.meta.MetaDataManager;
import org.scalingmq.route.meta.schema.PartitionMetadata;
import org.scalingmq.route.meta.schema.TopicMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 路由管理
 *
 * @author renyansong
 */
@Slf4j
public class RouteManager {

    private static final RouteManager INSTANCE = new RouteManager();

    private static final Gson GSON = new Gson();

    /**
     * SRV的后缀
     * <service name>.<namespace>.svc.cluster.local
     */
    private static final String SRV_NAME_SUFFIX = ".svc.cluster.local";

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
        List<PartitionMetadata> partitionMetadataList = scheduleStoragePods0(topicName, topicMetadata.getPartitionNums(), topicMetadata.getReplicateFactor());
        if (partitionMetadataList != null && partitionMetadataList.size() > 0) {
            String value = GSON.toJson(partitionMetadataList);
            // 更新元数据
            String jsonPatch =
                    """
                    [
                     { "op": "add", "path": "/partitionMetadataList", "value": %s}
                    ]
                    """.formatted(value);
            return MetaDataManager.getInstance().updateTopicMetadata(topicName, jsonPatch);
        }
        return false;
    }

    private List<PartitionMetadata> scheduleStoragePods0(String topicName, Integer partitions, Integer replicatorFactor) {
        K8sApiClient instance = K8sApiClient.getInstance();
        if (instance == null) {
            return Collections.emptyList();
        }
        // result
        List<PartitionMetadata> partitionMetadataList = new ArrayList<>();

        // partition的replicator数量
        int podNums = replicatorFactor + 1;

        for (int partition = 1; partition <= partitions; partition++) {
            // 创建service
            StorageServiceTemplate storageServiceTemplate = new StorageServiceTemplate(topicName, partition);
            storageServiceTemplate.getPorts().add(Integer.valueOf(RouteConfig.getInstance().getScheduleStoragePodPort()));
            storageServiceTemplate.getPorts().add(Integer.valueOf(RouteConfig.getInstance().getScheduleStoragePodRaftPort()));

            storageServiceTemplate.getPortNames().add(RouteConfig.getInstance().getScheduleStoragePodPortName());
            storageServiceTemplate.getPortNames().add(RouteConfig.getInstance().getScheduleStoragePodRaftPortName());

            boolean srvRes = instance.createService(
                    RouteConfig.getInstance().getNamespace(),
                    storageServiceTemplate.getStorageServiceName(),
                    StorageServiceTemplate.SPEC_SELECTOR_MAP,
                    storageServiceTemplate.getPorts(),
                    storageServiceTemplate.getPortNames(),
                    storageServiceTemplate.getClusterIp()
            );
            if (!srvRes) {
                return partitionMetadataList;
            }

            StoragePodTemplate storagePodTemplate = new StoragePodTemplate(storageServiceTemplate);
            storagePodTemplate.getContainerPorts().add(Integer.valueOf(RouteConfig.getInstance().getScheduleStoragePodPort()));
            storagePodTemplate.getContainerPorts().add(Integer.valueOf(RouteConfig.getInstance().getScheduleStoragePodRaftPort()));
            storagePodTemplate.getContainerPortNames().add(RouteConfig.getInstance().getScheduleStoragePodPortName());
            storagePodTemplate.getContainerPortNames().add(RouteConfig.getInstance().getScheduleStoragePodRaftPortName());

            storagePodTemplate.setReplicas(podNums);
            String scheduleStorageCoordinatorRatio = RouteConfig.getInstance().getScheduleStorageCoordinatorRatio();
            if (scheduleStorageCoordinatorRatio != null && !"".equals(scheduleStorageCoordinatorRatio)) {
                int coordinatorNums = podNums * Integer.parseInt(scheduleStorageCoordinatorRatio) / 100;
                if (coordinatorNums > 0) {
                    storagePodTemplate.setReplicas(podNums + coordinatorNums);
                    StringBuilder coordinatorStr = new StringBuilder();
                    for (int i = podNums; i < podNums + coordinatorNums; i++) {
                        coordinatorStr.append(",").append(i + 1);
                    }
                    storagePodTemplate.addCoordinatorConfig(coordinatorStr.toString());
                }
            }
            boolean podCreate = instance.createPods(
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
                    storagePodTemplate.getContainerPorts(),
                    storagePodTemplate.getContainerPortNames(),
                    StoragePodTemplate.ENV_MAP,
                    storagePodTemplate.getCpuResource(),
                    storagePodTemplate.getMemoryResource()
            );
            if (!podCreate) {
                return null;
            }

            List<String> partitionPodNameStrList = new ArrayList<>();
            for (int pod = 0; pod < podNums; pod++) {
                // 完整的可访问pod地址
                partitionPodNameStrList.add(storageServiceTemplate.getStoragePodName()
                        + "-" + pod // pod名 + statefulset序号
                        + "." + storageServiceTemplate.getStorageServiceName() + "." + RouteConfig.getInstance().getNamespace() // serviceName + namespace
                        + SRV_NAME_SUFFIX);
            }
            PartitionMetadata partitionMetadata = PartitionMetadata.builder()
                    .partitionNum(partition)
                    .storagePodNums(partitionPodNameStrList)
                    .build();

            partitionMetadataList.add(partitionMetadata);
        }

        return partitionMetadataList;
    }

}
