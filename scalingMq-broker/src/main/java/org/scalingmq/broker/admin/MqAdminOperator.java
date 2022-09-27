package org.scalingmq.broker.admin;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.conf.BrokerConfig;
import org.scalingmq.broker.exception.TopicAlreadyExistException;
import org.scalingmq.broker.exception.TopicCreateFailException;
import org.scalingmq.broker.server.http.req.CreateTopicReq;
import org.scalingmq.common.lifecycle.Lifecycle;
import org.scalingmq.common.utils.StopWatch;
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

    private static final RouteAppClient ROUTE_APP_CLIENT = RouteAppClient.getInstance();

    public MqAdminOperator() {
        init();
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
        StopWatch stopWatch = new StopWatch("createTopic");
        try {
            stopWatch.start("开始查询topic元数据(fetch topic metadata)");
            FetchTopicMetadataResultWrapper.FetchTopicMetadataResult fetchTopicMetadataResult
                    = ROUTE_APP_CLIENT.fetchTopicMetadata(
                    FetchTopicMetadataReqWrapper.FetchTopicMetadataReq.newBuilder()
                            .setTopicName(createTopicReq.getTopicName())
                            .build());
            stopWatch.stop();
            if (log.isDebugEnabled()) {
                log.debug("获取topic metadata为:{}", fetchTopicMetadataResult.toString());
            }
            String topicName = fetchTopicMetadataResult.getTopicName();
            if (!"".equals(topicName)) {
                log.debug(stopWatch.prettyPrint());
                throw new TopicAlreadyExistException();
            }
            stopWatch.start("开始新建topic元数据(add topic metadata)");
            // 新建topic
            boolean createResult = ROUTE_APP_CLIENT.createTopicMetadata(PutTopicMetadataReqWrapper.PutTopicMetadataReq.newBuilder()
                    .setTopicName(createTopicReq.getTopicName())
                    .setPartitionNum(createTopicReq.getPartitionNum())
                            .setReplicateFactor(createTopicReq.getReplicateFactor())
                    .build());
            if (!createResult) {
                stopWatch.stop();
                log.debug(stopWatch.prettyPrint());
                throw new TopicCreateFailException();
            }
            stopWatch.stop();
            stopWatch.start("调度路由服务,创建存储pods(schedule route app, create storage pods)");
            // 调用创建存储pod
            boolean storagePods = ROUTE_APP_CLIENT.schedStoragePods(SchedStoragePodReqWrapper.SchedStoragePodReq.newBuilder()
                    .setTopicName(createTopicReq.getTopicName())
                    .build());
            stopWatch.stop();
            log.debug(stopWatch.prettyPrint());
            return storagePods;
        } catch (TopicAlreadyExistException | TopicCreateFailException te) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
                log.debug(stopWatch.prettyPrint());
            }
            throw te;
        } catch (Exception e) {
            log.error("创建topic失败, 请求:{}", createTopicReq.toString(), e);
            if (stopWatch.isRunning()) {
                stopWatch.stop();
                log.debug(stopWatch.prettyPrint());
            }
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
