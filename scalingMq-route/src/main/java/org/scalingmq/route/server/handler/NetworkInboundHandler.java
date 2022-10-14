package org.scalingmq.route.server.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.route.client.entity.RouteReqWrapper;
import org.scalingmq.route.client.entity.RouteResWrapper;
import org.scalingmq.route.conf.RouteConfig;

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
public class NetworkInboundHandler extends SimpleChannelInboundHandler<RouteReqWrapper.RouteReq> {

    private static final ThreadPoolExecutor SERVICE_THREAD_POOL = new ThreadPoolExecutor(
            RouteConfig.getInstance().getServiceThreadCount(),
            RouteConfig.getInstance().getServiceThreadCount(),
            0L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1024),
            new ThreadFactory() {
                final AtomicInteger index = new AtomicInteger();
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "route-service-thread-" + index.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RouteReqWrapper.RouteReq msg) {
        log.debug("route netty handler 收到请求:{}", msg.toString());
        var req =
                switch (msg.getReqType().getNumber()) {
                    case RouteReqWrapper.RouteReq.ReqType.FETCH_TOPIC_METADATA_VALUE -> msg.getFetchTopicMetadataReq();
                    case RouteReqWrapper.RouteReq.ReqType.PUT_TOPIC_METADATA_VALUE -> msg.getPutTopicMetadataReq();
                    case RouteReqWrapper.RouteReq.ReqType.SCHED_STORAGE_POD_VALUE -> msg.getSchedStoragePodReq();
                    case RouteReqWrapper.RouteReq.ReqType.ISR_UPDATE_VALUE -> msg.getIsrUpdateReq();
                    default -> null;
                };
        if (req == null) {
            throw new RuntimeException("未找到对应API处理");
        }
        log.debug("route netty handler 使用处理类:{}", req.getClass().getName());

        SERVICE_THREAD_POOL.submit(() -> {
            RequestHandler handler = RequestHandlerFactory.getInstance().getHandler(req.getClass());
            handler.handle(req, ctx.channel());
        });
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
