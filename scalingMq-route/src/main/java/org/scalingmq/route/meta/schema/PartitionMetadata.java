package org.scalingmq.route.meta.schema;

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
public class PartitionMetadata {

    /**
     * 分区号
     */
    private Integer partitionNum;

    /**
     * 存储pod的序号集合
     */
    private List<Integer> storagePodNums;

    /**
     * in sync replicator 的pod序号集合
     */
    private List<Integer> isrStoragePodNums;

}
