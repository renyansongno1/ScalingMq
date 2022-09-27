package org.scalingmq.route.meta.schema;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 分区的元数据
 * @author renyansong
 */
@Getter
@Setter
@ToString
@Builder
public class PartitionMetadata {

    /**
     * 分区号
     */
    private Integer partitionNum;

    /**
     * 存储pod的集合
     */
    private List<String> storagePodNums;

    /**
     * in sync replicator 的pod集合
     */
    private List<String> isrStoragePodNums;

}
