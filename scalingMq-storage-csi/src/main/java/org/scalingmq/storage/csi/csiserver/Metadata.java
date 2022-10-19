package org.scalingmq.storage.csi.csiserver;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * csi的元数据
 * @author renyansong
 */
@Getter
@Setter
public class Metadata {

    private static final Metadata INSTANCE = new Metadata();

    public static Metadata getInstance() {
        return INSTANCE;
    }

    private Map<String, VolumeEntry> pvcVolumeRelation = new ConcurrentHashMap<>();

    private String nodeId = UUID.randomUUID().toString();


}
