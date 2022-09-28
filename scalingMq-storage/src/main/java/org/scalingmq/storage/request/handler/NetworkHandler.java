package org.scalingmq.storage.request.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.storage.api.StorageApiResWrapper;
import org.scalingmq.storage.exception.NotFoundRequestHandlerException;

/**
 * 网络事件处理器
 *
 * @author renyansong
 */
@ChannelHandler.Sharable
public class NetworkHandler extends SimpleChannelInboundHandler<StorageApiReqWrapper.StorageApiReq> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StorageApiReqWrapper.StorageApiReq msg) throws Exception {

        var req =
                switch (msg.getApiTypeValue()) {
                    case StorageApiReqWrapper.StorageApiReq.ApiType.PRODUCT_VALUE -> msg.getPutMsgReq();
                    case StorageApiReqWrapper.StorageApiReq.ApiType.FETCH_VALUE -> msg.getFetchMsgReq();
                    default -> null;
                };
        if (req == null) {
            throw new NotFoundRequestHandlerException();
        }
        RequestHandler handler = RequestHandlerFactory.getInstance().getHandler(req.getClass());
        ctx.writeAndFlush(handler.handle(req));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        StorageApiResWrapper.StorageApiRes storageApiRes = StorageApiResWrapper.StorageApiRes
                .newBuilder()
                .setErrorCode(cause.getClass().getName())
                .setErrorMsg(cause.getMessage())
                .build();
        ctx.writeAndFlush(storageApiRes).addListener(ChannelFutureListener.CLOSE);
    }

}
