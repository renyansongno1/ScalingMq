package org.scalingmq.storage.request.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.storage.api.StorageApiResWrapper;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.exception.NotFoundRequestHandlerException;
import org.scalingmq.storage.exception.StorageBaseException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 网络事件处理器
 *
 * @author renyansong
 */
@ChannelHandler.Sharable
@Slf4j
public class NetworkInBoundHandler extends SimpleChannelInboundHandler<StorageApiReqWrapper.StorageApiReq> {

    private static final ThreadPoolExecutor SERVICE_THREAD_POOL
            = new ThreadPoolExecutor(StorageConfig.getInstance().getServiceThreadCount(),
            StorageConfig.getInstance().getServiceThreadCount(),
            0L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1024),
            new ThreadFactory() {
                AtomicInteger index = new AtomicInteger();
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "storage-service-thread-" + index.getAndIncrement());
                }
            }, new ThreadPoolExecutor.CallerRunsPolicy());

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
        SERVICE_THREAD_POOL.submit(new Runnable() {
            @Override
            public void run() {
                RequestHandler handler = RequestHandlerFactory.getInstance().getHandler(req.getClass());
                handler.handle(req, ctx.channel());
            }
        });

    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("网络异常.", cause);
        if (cause instanceof StorageBaseException) {
            StorageApiResWrapper.StorageApiRes storageApiRes = StorageApiResWrapper.StorageApiRes.newBuilder()
                    .setErrorMsg(((StorageBaseException) cause).getMsg())
                    .setErrorCode(((StorageBaseException) cause).getCode().getCode())
                    .build();
            ctx.writeAndFlush(storageApiRes).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        // 通用返回
        StorageApiResWrapper.StorageApiRes storageApiRes = StorageApiResWrapper.StorageApiRes
                .newBuilder()
                .setErrorCode(cause.getClass().getName())
                .setErrorMsg(cause.getMessage())
                .build();
        ctx.writeAndFlush(storageApiRes).addListener(ChannelFutureListener.CLOSE);
    }

}
