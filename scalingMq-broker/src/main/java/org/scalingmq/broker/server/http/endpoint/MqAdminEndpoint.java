package org.scalingmq.broker.server.http.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.admin.MqAdminOperator;
import org.scalingmq.broker.server.http.EndpointProcessor;
import org.scalingmq.broker.server.http.HttpEndpoint;
import org.scalingmq.broker.server.http.RequestBody;
import org.scalingmq.broker.server.http.req.CreateTopicReq;
import org.scalingmq.broker.server.http.vo.CreateTopicResponse;

/**
 * admin相关的操作端点
 * @author renyansong
 */
@Slf4j
public class MqAdminEndpoint implements EndpointProcessor {

    /**
     * 创建topic
     * @return 创建响应
     */
    @HttpEndpoint("/scalingmq/v1/topic/createTopic")
    public CreateTopicResponse createTopic(@RequestBody CreateTopicReq createTopicReq) {
        log.info("创建topic API, 收到请求:{}", createTopicReq.toString());
        // create
        try {
            MqAdminOperator.getInstance().createTopic(createTopicReq);
        } catch (Exception e) {
            return CreateTopicResponse.builder()
                    .success(false)
                    .errMsg(e.getMessage())
                    .topicName(createTopicReq.getTopicName())
                    .build();
        }
        // response
        return CreateTopicResponse.builder()
                .success(true)
                .topicName(createTopicReq.getTopicName())
                .build();
    }

}
