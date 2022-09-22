package org.scalingmq.route.server.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.scalingmq.route.client.entity.RouteReqWrapper;
import org.scalingmq.route.client.entity.RouteResWrapper;

/**
 * 网络事件处理器
 *
 * @author renyansong
 */
@ChannelHandler.Sharable
public class NetworkHandler extends SimpleChannelInboundHandler<RouteReqWrapper.RouteReq> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RouteReqWrapper.RouteReq msg) throws Exception {

        var req =
                switch (msg.getReqType().getNumber()) {
                    case RouteReqWrapper.RouteReq.ReqType.FETCH_TOPIC_METADATA_VALUE -> msg.getFetchTopicMetadataReq();
                    // case StorageApiReqWrapper.StorageApiReq.ApiType.COMMIT_VALUE -> msg.get
                    default -> null;
                };
        if (req == null) {
            throw new RuntimeException("未找到对应API处理");
        }
        RequestHandler handler = RequestHandlerFactory.getInstance().getHandler(req.getClass());
        ctx.writeAndFlush(handler.handle(req));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        RouteResWrapper.RouteApiRes routeApiRes = RouteResWrapper.RouteApiRes
                .newBuilder()
                .setErrorMsg(cause.getMessage())
                .setErrorCode(RouteResWrapper.RouteApiRes.ErrorCode.UNKNOWN)
                .build();
        ctx.writeAndFlush(routeApiRes).addListener(ChannelFutureListener.CLOSE);
    }

}
