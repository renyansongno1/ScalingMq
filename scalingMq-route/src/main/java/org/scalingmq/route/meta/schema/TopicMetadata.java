package org.scalingmq.route.meta.schema;

import lombok.*;

import java.util.List;

/**
 * topic相关的元数据
 * @author renyansong
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TopicMetadata {

    /**
     * 配置元数据名称的前缀
     */
    public static final String CONF_NAME_PREFIX = "topic-metadata-";

    /**
     * 主题名称
     */
    private String topicName;

    /**
     * 分区数量
     */
    private String partitionNums;

    /**
     * 分区相关的元数据
     */
    private List<PartitionMetadata> partitionMetadataList;
}
