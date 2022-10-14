package org.scalingmq.route.server.handler.impl;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.route.client.entity.RouteResWrapper;
import org.scalingmq.route.client.entity.SchedStoragePodReqWrapper;
import org.scalingmq.route.manager.RouteManager;
import org.scalingmq.route.server.handler.RequestHandler;

/**
 * 调度存储节点创建的请求
 * @author renyansong
 */
@Slf4j
public class SchedStoragePodHandler implements RequestHandler<SchedStoragePodReqWrapper.SchedStoragePodReq> {

    @Override
    public void handle(SchedStoragePodReqWrapper.SchedStoragePodReq schedStoragePodReq, Channel channel) {
        log.debug("收到调度创建存储pod的请求,topic:{}", schedStoragePodReq.getTopicName());
        boolean result = RouteManager.getInstance().scheduleStoragePods(schedStoragePodReq.getTopicName());
        channel.writeAndFlush(RouteResWrapper.RouteApiRes.newBuilder().setSchedStoragePodRes(result).build());
    }

}
