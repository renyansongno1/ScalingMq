package org.scalingmq.broker.server.http.req;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 创建topic的请求
 * @author renyansong
 */
@Getter
@Setter
@ToString
public class CreateTopicReq {

    /**
     * topic名称
     */
    private String topicName;

    /**
     * 分区数
     */
    private Integer partitionNum;

    /**
     * 副本系数，后面的副本选举是raft 所以最好副本数有两个及以上的双数（因为还有一个主副本）
     */
    private Integer replicateFactor = 2;

}
