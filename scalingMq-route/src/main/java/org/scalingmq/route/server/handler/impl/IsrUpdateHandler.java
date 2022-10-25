package org.scalingmq.route.server.handler.impl;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.route.client.entity.IsrUpdateReqWrapper;
import org.scalingmq.route.client.entity.RouteResWrapper;
import org.scalingmq.route.manager.RouteManager;
import org.scalingmq.route.server.handler.RequestHandler;

/**
 * isr的更新请求处理
 * @author renyansong
 */
@Slf4j
public class IsrUpdateHandler implements RequestHandler<IsrUpdateReqWrapper.IsrUpdateReq> {

    @Override
    public void handle(IsrUpdateReqWrapper.IsrUpdateReq isrUpdateReq, Channel channel) {
        try {
            RouteManager.getInstance().updateIsrMetadata(isrUpdateReq);
        } catch (Exception e) {
            log.error("处理isr update请求异常:{}", isrUpdateReq, e);
            RouteResWrapper.RouteApiRes res = RouteResWrapper.RouteApiRes.newBuilder()
                    .setErrorCode(RouteResWrapper.RouteApiRes.ErrorCode.UNKNOWN)
                    .setErrorMsg(e.getMessage())
                    .build();
            channel.writeAndFlush(res);
        }
        channel.writeAndFlush(RouteResWrapper.RouteApiRes.newBuilder().build());
    }

}
