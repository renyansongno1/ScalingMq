package org.scalingmq.route.server.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.route.client.entity.RouteReqWrapper;
import org.scalingmq.route.client.entity.RouteResWrapper;

/**
 * 网络事件处理器
 *
 * @author renyansong
 */
@ChannelHandler.Sharable
@Slf4j
public class NetworkHandler extends SimpleChannelInboundHandler<RouteReqWrapper.RouteReq> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RouteReqWrapper.RouteReq msg) throws Exception {
        log.debug("route netty handler 收到请求:{}", msg.toString());
        var req =
                switch (msg.getReqType().getNumber()) {
                    case RouteReqWrapper.RouteReq.ReqType.FETCH_TOPIC_METADATA_VALUE -> msg.getFetchTopicMetadataReq();
                    case RouteReqWrapper.RouteReq.ReqType.PUT_TOPIC_METADATA_VALUE -> msg.getPutTopicMetadataReq();
                    case RouteReqWrapper.RouteReq.ReqType.SCHED_STORAGE_POD_VALUE -> msg.getSchedStoragePodReq();
                    default -> null;
                };
        if (req == null) {
            throw new RuntimeException("未找到对应API处理");
        }
        log.debug("route netty handler 使用处理类:{}", req.getClass().getName());

        RequestHandler handler = RequestHandlerFactory.getInstance().getHandler(req.getClass());
        ctx.writeAndFlush(handler.handle(req));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("route netty catch error", cause);
        RouteResWrapper.RouteApiRes routeApiRes = RouteResWrapper.RouteApiRes
                .newBuilder()
                .setErrorMsg(cause.getMessage())
                .setErrorCode(RouteResWrapper.RouteApiRes.ErrorCode.UNKNOWN)
                .build();
        ctx.writeAndFlush(routeApiRes).addListener(ChannelFutureListener.CLOSE);
    }

}
