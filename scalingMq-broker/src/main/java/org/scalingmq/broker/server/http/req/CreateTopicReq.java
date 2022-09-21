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

}
