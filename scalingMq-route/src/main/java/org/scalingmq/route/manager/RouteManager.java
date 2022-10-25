package org.scalingmq.route.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.kubernetes.api.K8sApiClient;
import org.scalingmq.route.client.entity.IsrUpdateReqWrapper;
import org.scalingmq.route.conf.RouteConfig;
import org.scalingmq.route.manager.template.StoragePodTemplate;
import org.scalingmq.route.manager.template.StorageServiceTemplate;
import org.scalingmq.route.meta.MetaDataManager;
import org.scalingmq.route.meta.schema.PartitionMetadata;
import org.scalingmq.route.meta.schema.TopicMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    /**
     * 本地创建pv标识
     */
    private static final String LOCAL_PV_FLAG = "LOCAL";

    /**
     * 默认的本地创建storage classname
     */
    private static final String MANUAL_STORAGE_CLASS = "hostpath";

    /**
     * scalingmq csi
     */
    private static final String SCALINGMQ_STORAGE_CLASS = "org.scalingmq.csi.storage.class";

    /**
     * isr更新的term缓存
     */
    private static int CACHED_ISR_UPDATE_TERM = 0;

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
        if (log.isDebugEnabled()) {
            log.debug("开始为:{}, 调用存储pod", topicName);
        }
        // 查询topic元数据
        TopicMetadata topicMetadata = MetaDataManager.getInstance().getTopicMetadata(topicName);
        if (topicMetadata == null) {
            if (log.isDebugEnabled()) {
                log.debug("topic元数据为空, 跳过调度pods");
            }
            return false;
        }
        // 调出创建存储节点的pod
        List<PartitionMetadata> partitionMetadataList = scheduleStoragePods0(topicName, topicMetadata.getPartitionNums(), topicMetadata.getReplicateFactor());
        if (partitionMetadataList != null && partitionMetadataList.size() > 0) {
            String value = GSON.toJson(partitionMetadataList);
            // 更新元数据
            topicMetadata.setPartitionMetadataList(value);
            String jsonPatch = GSON.toJson(topicMetadata);
            if (log.isDebugEnabled()) {
                log.debug("开始patch configmap :{}", jsonPatch);
            }
            return MetaDataManager.getInstance().updateTopicMetadata(topicName, jsonPatch);
        }
        return false;
    }

    /**
     * 更新ISR操作
     * @param req isr更新请求
     */
    public void updateIsrMetadata(IsrUpdateReqWrapper.IsrUpdateReq req) {
        // TODO: 2022/10/14 元数据的更新 需要使用raft协议支持 不然集群后的route节点没有leader 数据不一致
        if (log.isDebugEnabled()) {
            log.debug("收到isr的更新请求:{}", req);
        }
        if (req.getTerm() < CACHED_ISR_UPDATE_TERM) {
            log.warn("收到了小于上次更新的isr元数据的term:{}, 上次term:{}, 忽略更新", req.getTerm(), CACHED_ISR_UPDATE_TERM);
            return;
        }
        CACHED_ISR_UPDATE_TERM = req.getTerm();
        // 查询元数据
        TopicMetadata topicMetadata = MetaDataManager.getInstance().getTopicMetadata(req.getTopicName());
        if (topicMetadata == null) {
            log.error("查询topic:{} 元数据为空, 上报isr元数据:{} 失败", req.getTopicName(), req);
            return;
        }
        List<PartitionMetadata> partitionMetadataList = GSON.fromJson(topicMetadata.getPartitionMetadataList(), new TypeToken<List<PartitionMetadata>>() {}.getType());
        PartitionMetadata partitionMetadata = partitionMetadataList.get(req.getPartitionNum() - 1);
        if (partitionMetadata == null) {
            log.error("收到不存在的partition更新isr, 当前数据:{}, partition更新请求:{}", topicMetadata, req);
            return;
        }
        partitionMetadata.setIsrStoragePodNums(req.getIsrAddrsList());

        topicMetadata.setPartitionMetadataList(GSON.toJson(partitionMetadataList));
        // 更新元数据
        String jsonPatch = GSON.toJson(topicMetadata);
        MetaDataManager.getInstance().updateTopicMetadata(req.getTopicName(), jsonPatch);
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

            // 设置topic的一些元数据环境变量 传递下去
            storagePodTemplate.addPartitionNumConfig(partition);
            storagePodTemplate.addTopicConfig(topicName);
            // 继续添加其他的数据
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

            // pod的存储卷挂载
            mountVolume(storagePodTemplate, topicName, partition, instance);

            // 开始创建pod
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
                    storagePodTemplate.getMemoryResource(),
                    storagePodTemplate.getInitStorageSize(),
                    storagePodTemplate.getMountPath(),
                    storagePodTemplate.getStorageClassName()
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
                    .isrStoragePodNums(new ArrayList<>(0))
                    .build();

            partitionMetadataList.add(partitionMetadata);
        }

        return partitionMetadataList;
    }

    /**
     * 给storage pod挂载volume
     */
    private void mountVolume(StoragePodTemplate storagePodTemplate, String topicName, Integer partition, K8sApiClient client) {
        String storageClassName;
        if (RouteConfig.getInstance().getScheduleStoragePvcType().equals(LOCAL_PV_FLAG)) {
            storageClassName = MANUAL_STORAGE_CLASS;
            // 创建pv
            String pvId = UUID.randomUUID().toString().replaceAll("-", "");
            boolean createPvRes = client.createPv(RouteConfig.getInstance().getNamespace(),
                    storagePodTemplate.getPodName() + "-" + pvId,
                    RouteConfig.getInstance().getDefaultStorageInitSize() * storagePodTemplate.getReplicas() + "Gi",
                    storageClassName,
                    RouteConfig.getInstance().getLocalPvcHostPath());
            if (!createPvRes) {
                // 创建失败
                throw new RuntimeException("create local pv fail");
            }
        } else {
            // 使用 csi driver
            storageClassName = SCALINGMQ_STORAGE_CLASS;
        }
        storagePodTemplate.setStorageClassName(storageClassName);
        storagePodTemplate.setInitStorageSize(RouteConfig.getInstance().getDefaultStorageInitSize() + "Gi");
        storagePodTemplate.setMountPath(RouteConfig.getInstance().getStoragePodMountPathPrefix() + topicName + "/" + partition);
    }

}
