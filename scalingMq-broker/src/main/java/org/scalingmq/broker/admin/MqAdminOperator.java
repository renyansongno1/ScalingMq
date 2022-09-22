package org.scalingmq.broker.admin;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.server.http.req.CreateTopicReq;

/**
 * admin类操作
 * @author renyansong
 */
@Slf4j
public class MqAdminOperator {

    private static final MqAdminOperator INSTANCE = new MqAdminOperator();

    private MqAdminOperator() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static MqAdminOperator getInstance() {
        return INSTANCE;
    }

    /**
     * 创建topic
     * @param createTopicReq req
     */
    public void createTopic(CreateTopicReq createTopicReq) {

    }

}
