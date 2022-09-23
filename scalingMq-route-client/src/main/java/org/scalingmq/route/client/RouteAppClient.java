package org.scalingmq.route.client;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.net.NetworkClient;
import org.scalingmq.route.client.conf.RouteClientConfig;
import org.scalingmq.route.client.entity.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 路由应用client端
 *
 * @author renyansong
 */
@Slf4j
public class RouteAppClient {

    private static final RouteAppClient INSTANCE = new RouteAppClient();

    private RouteClientConfig routeClientConfig;

    private ThreadPoolExecutor netThreadPool;

    private volatile boolean init = false;

    private RouteAppClient() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static RouteAppClient getInstance() {
        return INSTANCE;
    }

    /**
     * 设置配置
     *
     * @param routeClientConfig 配置数据
     */
    public void initConfig(RouteClientConfig routeClientConfig) {
        if (!init) {
            synchronized (this) {
                if (init) {
                    return;
                }
                this.routeClientConfig = routeClientConfig;
                // 初始化线程池
                netThreadPool = new ThreadPoolExecutor(routeClientConfig.getThreadCount(),
                        routeClientConfig.getThreadCount(),
                        0L,
                        TimeUnit.MILLISECONDS,
                        new ArrayBlockingQueue<>(100),
                        new ThreadFactory() {
                            final AtomicInteger index = new AtomicInteger(0);

                            @Override
                            public Thread newThread(Runnable r) {
                                return new Thread(r, "route-client-net-thread-" + index.getAndIncrement());
                            }
                        });
                init = true;
            }
        }

    }

    /**
     * 拉取topic元数据
     *
     * @param req 拉取请求
     * @return topic元数据
     */
    public FetchTopicMetadataResultWrapper.FetchTopicMetadataResult fetchTopicMetadata(
            FetchTopicMetadataReqWrapper.FetchTopicMetadataReq req) throws Exception {
        RouteReqWrapper.RouteReq routeReq = RouteReqWrapper.RouteReq.newBuilder()
                .setReqType(RouteReqWrapper.RouteReq.ReqType.FETCH_TOPIC_METADATA)
                .setFetchTopicMetadataReq(req)
                .build();
        RouteResWrapper.RouteApiRes res = netThreadPool.submit(new NetCallTask(routeReq)).get();
        log.debug("拉取topic元数据结果:{}", res.toString());
        if (!"".equals(res.getErrorMsg())) {
            log.error("拉取topic元数据异常:{}", res.getErrorMsg());
        }
        return res.getFetchTopicMetadataResult();
    }

    /**
     * 创建topic元数据
     * @param req 请求
     * @return 操作结果
     */
    public boolean createTopicMetadata(PutTopicMetadataReqWrapper.PutTopicMetadataReq req) throws Exception {
        RouteReqWrapper.RouteReq routeReq = RouteReqWrapper.RouteReq.newBuilder()
                .setReqType(RouteReqWrapper.RouteReq.ReqType.PUT_TOPIC_METADATA)
                .setPutTopicMetadataReq(req)
                .build();
        RouteResWrapper.RouteApiRes res = netThreadPool.submit(new NetCallTask(routeReq)).get();
        log.debug("创建topic元数据结果:{}", res.toString());
        if (!"".equals(res.getErrorMsg())) {
            log.error("创建topic元数据异常:{}", res.getErrorMsg());
            return false;
        }
        return res.getCreateTopicMetadataRes();
    }

    /**
     * 异步调用任务
     */
    private class NetCallTask implements Callable<RouteResWrapper.RouteApiRes> {

        private final Object req;

        private NetCallTask(Object req) {
            this.req = req;
        }

        @Override
        public RouteResWrapper.RouteApiRes call() {
            log.debug("开始请求网络....");
            return (RouteResWrapper.RouteApiRes) NetworkClient.getInstance().sendReq(req,
                    routeClientConfig.getServerAddr(),
                    routeClientConfig.getServerPort(),
                    RouteResWrapper.RouteApiRes.getDefaultInstance());
        }

    }

}
