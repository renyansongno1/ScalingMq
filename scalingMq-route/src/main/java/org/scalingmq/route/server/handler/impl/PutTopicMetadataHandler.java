package org.scalingmq.route.server.handler.impl;

import org.scalingmq.route.client.entity.PutTopicMetadataReqWrapper;
import org.scalingmq.route.client.entity.RouteResWrapper;
import org.scalingmq.route.meta.MetaDataManager;
import org.scalingmq.route.server.handler.RequestHandler;

/**
 * topic创建请求的handler
 * @author renyansong
 */
public class PutTopicMetadataHandler implements RequestHandler<PutTopicMetadataReqWrapper.PutTopicMetadataReq, RouteResWrapper.RouteApiRes> {

    @Override
    public RouteResWrapper.RouteApiRes handle(PutTopicMetadataReqWrapper.PutTopicMetadataReq putTopicMetadataReq) {
        boolean result = MetaDataManager.getInstance().createTopicMetadata(putTopicMetadataReq.getTopicName(), putTopicMetadataReq.getPartitionNum());
        return RouteResWrapper.RouteApiRes.newBuilder().setCreateTopicMetadataRes(result).build();
    }
}
