package org.scalingmq.route.manager.template;

import lombok.Getter;
import lombok.Setter;
import org.scalingmq.route.conf.RouteConfig;

import java.util.HashMap;
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
        ENV_MAP.put("LANG", "en_US.UTF-8");

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

    private Integer containerPort = Integer.valueOf(RouteConfig.getInstance().getScheduleStoragePodPort());

    private String cpuResource = RouteConfig.getInstance().getScheduleStoragePodCpu();

    private String memoryResource = RouteConfig.getInstance().getScheduleStoragePodMem();

}
