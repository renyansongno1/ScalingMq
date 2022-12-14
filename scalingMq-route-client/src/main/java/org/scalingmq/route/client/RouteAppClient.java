package org.scalingmq.route.client;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.cache.LocalCache;
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

    private LocalCache<FetchTopicMetadataReqWrapper.FetchTopicMetadataReq,
            FetchTopicMetadataResultWrapper.FetchTopicMetadataResult> localCache;

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
                localCache = LocalCache.<FetchTopicMetadataReqWrapper.FetchTopicMetadataReq,
                                FetchTopicMetadataResultWrapper.FetchTopicMetadataResult>builder()
                        .cacheCount(100)
                        .expireTime(300L)
                        .timeUnit(TimeUnit.SECONDS)
                        .cacheLoader(key -> fetchTopicMetadataFromRemote((FetchTopicMetadataReqWrapper.FetchTopicMetadataReq) key))
                        .build();
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
        // 先查询缓存 cache 不存在会查询远端
        return localCache.get(req);
    }

    private FetchTopicMetadataResultWrapper.FetchTopicMetadataResult
    fetchTopicMetadataFromRemote(FetchTopicMetadataReqWrapper.FetchTopicMetadataReq req)
            throws InterruptedException, ExecutionException {
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
     * 调度storage pod
     * @param req 请求
     * @return 操作结果
     */
    public boolean schedStoragePods(SchedStoragePodReqWrapper.SchedStoragePodReq req) throws Exception {
        RouteReqWrapper.RouteReq routeReq = RouteReqWrapper.RouteReq.newBuilder()
                .setReqType(RouteReqWrapper.RouteReq.ReqType.SCHED_STORAGE_POD)
                .setSchedStoragePodReq(req)
                .build();
        RouteResWrapper.RouteApiRes res = netThreadPool.submit(new NetCallTask(routeReq)).get();
        if (!"".equals(res.getErrorMsg())) {
            log.error("调度storage pods异常:{}", res.getErrorMsg());
            return false;
        }
        return res.getSchedStoragePodRes();
    }

    /**
     * 上报isr元数据
     * @param req isr更新请求
     * @throws Exception 异常信息
     */
    public void reportIsrData(IsrUpdateReqWrapper.IsrUpdateReq req) throws Exception {
        RouteReqWrapper.RouteReq routeReq = RouteReqWrapper.RouteReq.newBuilder()
                .setReqType(RouteReqWrapper.RouteReq.ReqType.ISR_UPDATE)
                .setIsrUpdateReq(req)
                .build();
        RouteResWrapper.RouteApiRes res = null;
        try {
            res = netThreadPool.submit(new NetCallTask(routeReq)).get(3000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("上报isr元数据异常:{}", req, e);
            return;
        }
        if (!"".equals(res.getErrorMsg())) {
            log.error("上报isr元数据失败, route异常:{}", res.getErrorMsg());
        }
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
            if (log.isDebugEnabled()) {
                log.debug("开始请求网络....");
            }
            return (RouteResWrapper.RouteApiRes) NetworkClient.getInstance().sendReq(req,
                    routeClientConfig.getServerAddr(),
                    routeClientConfig.getServerPort(),
                    RouteResWrapper.RouteApiRes.getDefaultInstance());
        }

    }

}
