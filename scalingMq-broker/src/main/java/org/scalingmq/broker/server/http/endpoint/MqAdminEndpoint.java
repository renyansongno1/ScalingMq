package org.scalingmq.broker.server.http.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.admin.MqAdminOperator;
import org.scalingmq.broker.server.http.EndpointProcessor;
import org.scalingmq.broker.server.http.HttpEndpoint;
import org.scalingmq.broker.server.http.RequestBody;
import org.scalingmq.broker.server.http.req.CreateTopicReq;
import org.scalingmq.broker.server.http.vo.CreateTopicResponse;
import org.scalingmq.common.ioc.IocContainer;

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
        if (log.isDebugEnabled()) {
            log.debug("创建topic API, 收到请求:{}", createTopicReq.toString());
        }
        // create
        boolean result;
        try {
            result = IocContainer.getInstance().getObj(MqAdminOperator.class).createTopic(createTopicReq);
        } catch (Exception e) {
            return CreateTopicResponse.builder()
                    .success(false)
                    .errMsg(e.getMessage())
                    .topicName(createTopicReq.getTopicName())
                    .build();
        }
        // response
        return CreateTopicResponse.builder()
                .success(result)
                .topicName(createTopicReq.getTopicName())
                .build();
    }

}
