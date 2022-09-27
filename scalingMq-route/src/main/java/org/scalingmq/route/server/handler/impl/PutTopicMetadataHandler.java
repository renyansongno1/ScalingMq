package org.scalingmq.route.server.handler.impl;

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
public class PutTopicMetadataHandler implements RequestHandler<PutTopicMetadataReqWrapper.PutTopicMetadataReq, RouteResWrapper.RouteApiRes> {

    @Override
    public RouteResWrapper.RouteApiRes handle(PutTopicMetadataReqWrapper.PutTopicMetadataReq putTopicMetadataReq) {
        log.debug("创建topic元数据请求:{}", putTopicMetadataReq.toString());
        boolean result = MetaDataManager.getInstance().createTopicMetadata(putTopicMetadataReq.getTopicName(), putTopicMetadataReq.getPartitionNum(), putTopicMetadataReq.getReplicateFactor());
        return RouteResWrapper.RouteApiRes.newBuilder().setCreateTopicMetadataRes(result).build();
    }
}
