package org.scalingmq.route.server.handler.impl;

import io.netty.channel.Channel;
import org.scalingmq.route.client.entity.IsrUpdateReqWrapper;
import org.scalingmq.route.client.entity.RouteResWrapper;
import org.scalingmq.route.manager.RouteManager;
import org.scalingmq.route.server.handler.RequestHandler;

/**
 * isr的更新请求处理
 * @author renyansong
 */
public class IsrUpdateHandler implements RequestHandler<IsrUpdateReqWrapper.IsrUpdateReq> {

    @Override
    public void handle(IsrUpdateReqWrapper.IsrUpdateReq isrUpdateReq, Channel channel) {
        RouteManager.getInstance().updateIsrMetadata(isrUpdateReq);
        channel.writeAndFlush(RouteResWrapper.RouteApiRes.newBuilder().build());
    }

}
