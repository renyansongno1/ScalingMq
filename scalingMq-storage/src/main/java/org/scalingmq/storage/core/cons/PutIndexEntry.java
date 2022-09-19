package org.scalingmq.storage.core.cons;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 写入索引的条目
 * @author renyansong
 */
@Getter
@Setter
@Builder
public class PutIndexEntry {

    /**
     * 存储介质的编号
     */
    private Integer storagePriorityFlag;

    /**
     * 存储介质的物理偏移量
     */
    private Long storageOffset;

    /**
     * 存储长度
     */
    private Integer msgSize;

}
