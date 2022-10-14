package org.scalingmq.route.server.handler.impl;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.route.client.entity.PutTopicMetadataReqWrapper;
import org.scalingmq.route.client.entity.RouteResWrapper;
import org.scalingmq.route.meta.MetaDataManager;
import org.scalingmq.route.server.handler.RequestHandler;

/**
 * topic创建请求的handler
 * @author renyansong
 */
@Slf4j
public class PutTopicMetadataHandler implements RequestHandler<PutTopicMetadataReqWrapper.PutTopicMetadataReq> {

    @Override
    public void handle(PutTopicMetadataReqWrapper.PutTopicMetadataReq putTopicMetadataReq, Channel channel) {
        log.debug("创建topic元数据请求:{}", putTopicMetadataReq.toString());
        boolean result = MetaDataManager.getInstance().createTopicMetadata(putTopicMetadataReq.getTopicName(), putTopicMetadataReq.getPartitionNum(), putTopicMetadataReq.getReplicateFactor());
        channel.writeAndFlush(RouteResWrapper.RouteApiRes.newBuilder().setCreateTopicMetadataRes(result).build());
    }
}
