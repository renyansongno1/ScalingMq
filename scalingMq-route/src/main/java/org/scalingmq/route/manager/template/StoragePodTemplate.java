package org.scalingmq.route.manager.template;

import lombok.Getter;
import lombok.Setter;
import org.scalingmq.route.conf.RouteConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储pod的模板
 * @author renyansong
 */
@Getter
@Setter
public class StoragePodTemplate {

    public static final Map<String, String> META_LABEL_MAP = new HashMap<>();

    public static final Map<String, String> SPEC_LABEL_MAP = new HashMap<>();

    public static final Map<String, String> TEMP_LABEL_MAP = new HashMap<>();

    public static final Map<String, String> ENV_MAP = new HashMap<>();

    private final StorageServiceTemplate storageServiceTemplate;

    public StoragePodTemplate(StorageServiceTemplate storageServiceTemplate) {
        this.storageServiceTemplate = storageServiceTemplate;
        META_LABEL_MAP.put("app", storageServiceTemplate.getStoragePodName());
        SPEC_LABEL_MAP.put("app", storageServiceTemplate.getStoragePodName());
        TEMP_LABEL_MAP.put("app", storageServiceTemplate.getStoragePodName());
        ENV_MAP.put("SERVICE_NAME", storageServiceTemplate.getStorageServiceName());
        ENV_MAP.put("AFT_PORT", RouteConfig.getInstance().getScheduleStoragePodPort());
        ENV_MAP.put("TZ", "Asia/Shanghai");
        ENV_MAP.put("LANG", "C.utf8");
        // 设置route本身的地址信息
        ENV_MAP.put("ROUTE_SERVER_ADDR", "scalingmq-route-service");
        ENV_MAP.put("ROUTE_SERVER_PORT", "5432");

        this.serviceName = storageServiceTemplate.getStorageServiceName();
        this.templateMetadataName = storageServiceTemplate.getStoragePodName();
        this.containerName = storageServiceTemplate.getStoragePodName();
        this.podName = storageServiceTemplate.getStoragePodName();
    }

    private String podName;

    /**
     * 副本数
     */
    private Integer replicas;

    /**
     * 服务名
     */
    private String serviceName;

    private String templateMetadataName;

    private String containerName;

    private String imageName = RouteConfig.getInstance().getScheduleStoragePodImage();

    private List<Integer> containerPorts = new ArrayList<>();

    private List<String> containerPortNames = new ArrayList<>();

    private String cpuResource = RouteConfig.getInstance().getScheduleStoragePodCpu();

    private String memoryResource = RouteConfig.getInstance().getScheduleStoragePodMem();

    private String storageClassName;

    private String mountPath;

    private String initStorageSize;

    /**
     * 添加coordinator的环境变量配置
     */
    public void addCoordinatorConfig(String str) {
        ENV_MAP.put("COORDINATOR_NUMS", str);
    }

    /**
     * 添加topicName的环境变量
     */
    public void addTopicConfig(String topicName) {
        ENV_MAP.put("TOPIC_NAME", topicName);
    }

    /**
     * 添加分区号的环境变量
     */
    public void addPartitionNumConfig(Integer partitionNum) {
        ENV_MAP.put("PARTITION_NUM", String.valueOf(partitionNum));
    }

}
