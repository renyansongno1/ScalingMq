package org.scalingmq.broker.admin;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.conf.BrokerConfig;
import org.scalingmq.broker.exception.TopicAlreadyExistException;
import org.scalingmq.broker.exception.TopicCreateFailException;
import org.scalingmq.broker.server.http.req.CreateTopicReq;
import org.scalingmq.common.lifecycle.Lifecycle;
import org.scalingmq.route.client.RouteAppClient;
import org.scalingmq.route.client.conf.RouteClientConfig;
import org.scalingmq.route.client.entity.FetchTopicMetadataReqWrapper;
import org.scalingmq.route.client.entity.FetchTopicMetadataResultWrapper;
import org.scalingmq.route.client.entity.PutTopicMetadataReqWrapper;
import org.scalingmq.route.client.entity.SchedStoragePodReqWrapper;

/**
 * admin类操作
 *
 * @author renyansong
 */
@Slf4j
public class MqAdminOperator implements Lifecycle {

    private static final MqAdminOperator INSTANCE = new MqAdminOperator();

    private static final RouteAppClient ROUTE_APP_CLIENT = RouteAppClient.getInstance();

    private MqAdminOperator() {
        /*if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }*/
        init();
    }

    public static MqAdminOperator getInstance() {
        return INSTANCE;
    }

    private void init() {
        RouteAppClient.getInstance().initConfig(
                RouteClientConfig.builder()
                        .serverAddr(BrokerConfig.getInstance().getRouteServerAddr())
                        .serverPort(Integer.valueOf(BrokerConfig.getInstance().getRouteServerPort()))
                        .threadCount(BrokerConfig.getInstance().getRouteClientThreadCount())
                        .build()
        );
    }

    /**
     * 创建topic
     *
     * @param createTopicReq req
     */
    public boolean createTopic(CreateTopicReq createTopicReq) throws Exception {
        // 查询是否存在topic了
        try {
            FetchTopicMetadataResultWrapper.FetchTopicMetadataResult fetchTopicMetadataResult
                    = ROUTE_APP_CLIENT.fetchTopicMetadata(
                    FetchTopicMetadataReqWrapper.FetchTopicMetadataReq.newBuilder()
                            .setTopicName(createTopicReq.getTopicName())
                            .build());
            log.debug("获取topic metadata为:{}", fetchTopicMetadataResult.toString());
            String topicName = fetchTopicMetadataResult.getTopicName();
            if (!"".equals(topicName)) {
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
            // 调用创建存储pod
            return ROUTE_APP_CLIENT.schedStoragePods(SchedStoragePodReqWrapper.SchedStoragePodReq.newBuilder()
                    .setTopicName(createTopicReq.getTopicName())
                    .build());
        } catch (TopicAlreadyExistException | TopicCreateFailException te) {
            throw te;
        } catch (Exception e) {
            log.error("创建topic失败, 请求:{}", createTopicReq.toString(), e);
            return false;
        }
    }

    @Override
    public void componentStart() {
        init();
    }

    @Override
    public void componentStop() {

    }
}
