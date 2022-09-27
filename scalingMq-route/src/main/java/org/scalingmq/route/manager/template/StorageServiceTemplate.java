package org.scalingmq.route.manager.template;

import lombok.Getter;
import lombok.Setter;
import org.scalingmq.route.conf.RouteConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储服务模板
 * @author renyansong
 */
@Getter
@Setter
public class StorageServiceTemplate {

    private String storageServiceName = "scalingmq-storage-service-headless";

    private String storagePodName = "scalingmq-storage";

    public static final Map<String, String> SPEC_SELECTOR_MAP = new HashMap<>();

    private final String topicName;

    public StorageServiceTemplate(String topicName, Integer partitionNum) {
        this.topicName = topicName;
        storagePodName += ("-" + topicName + "-" + partitionNum);
        storageServiceName += ("-" + topicName + "-" + partitionNum);
        SPEC_SELECTOR_MAP.put("app", storagePodName);
    }

    private List<Integer> ports = new ArrayList<>();

    private List<String> portNames = new ArrayList<>();

    private String clusterIp = "None";

}
