package org.scalingmq.broker.admin;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.conf.BrokerConfig;
import org.scalingmq.broker.exception.TopicAlreadyExistException;
import org.scalingmq.broker.exception.TopicCreateFailException;
import org.scalingmq.broker.server.http.req.CreateTopicReq;
import org.scalingmq.route.client.RouteAppClient;
import org.scalingmq.route.client.conf.RouteClientConfig;
import org.scalingmq.route.client.entity.FetchTopicMetadataReqWrapper;
import org.scalingmq.route.client.entity.FetchTopicMetadataResultWrapper;
import org.scalingmq.route.client.entity.PutTopicMetadataReqWrapper;

/**
 * admin类操作
 *
 * @author renyansong
 */
@Slf4j
public class MqAdminOperator {

    private static final MqAdminOperator INSTANCE = new MqAdminOperator();

    private static final RouteAppClient ROUTE_APP_CLIENT = RouteAppClient.getInstance();

    private MqAdminOperator() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
        init();
    }

    public static MqAdminOperator getInstance() {
        return INSTANCE;
    }

    private void init() {
        ROUTE_APP_CLIENT.initConfig(
                RouteClientConfig.builder()
                        .serverAddr(BrokerConfig.getInstance().getRouteServerAddr())
                        .serverPort(BrokerConfig.getInstance().getRouteServerPort())
                        .threadCount(BrokerConfig.getInstance().getRouteClientThreadCount())
                        .build()
        );
    }

    /**
     * 创建topic
     *
     * @param createTopicReq req
     */
    public void createTopic(CreateTopicReq createTopicReq) throws Exception {
        // 查询是否存在topic了
        try {
            FetchTopicMetadataResultWrapper.FetchTopicMetadataResult fetchTopicMetadataResult
                    = ROUTE_APP_CLIENT.fetchTopicMetadata(
                            FetchTopicMetadataReqWrapper.FetchTopicMetadataReq.newBuilder()
                                .setTopicName(createTopicReq.getTopicName())
                                .build());
            if (fetchTopicMetadataResult != null) {
                throw new TopicAlreadyExistException();
            }
            // 新建topic
            boolean createResult = ROUTE_APP_CLIENT.createTopicMetadata(PutTopicMetadataReqWrapper.PutTopicMetadataReq.newBuilder()
                    .setTopicName(createTopicReq.getTopicName())
                    .setPartitionNum(createTopicReq.getPartitionNum())
                    .build());
            if (!createResult) {
                throw new TopicCreateFailException();
            }
            // TODO: 2022/9/22 调用创建存储pod
        } catch (TopicAlreadyExistException | TopicCreateFailException te) {
            throw te;
        } catch (Exception e) {
            log.error("创建topic失败, 请求:{}", createTopicReq.toString(), e);
        }
    }

}
