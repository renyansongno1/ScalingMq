package org.scalingmq.storage.csi.csiserver;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 针对volume的数据实例
 * @author renyansong
 */
@Getter
@Setter
@ToString
public class VolumeEntry {

    /**
     * volume的id
     */
    private String volumeId;

    /**
     * volume的容量
     */
    private Long capacityBytes;

}
