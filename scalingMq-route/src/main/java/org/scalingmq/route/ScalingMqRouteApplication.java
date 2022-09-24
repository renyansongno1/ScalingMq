package org.scalingmq.route;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.config.ConfigParseUtil;
import org.scalingmq.common.ioc.IocContainer;
import org.scalingmq.common.lifecycle.Lifecycle;
import org.scalingmq.route.conf.RouteConfig;
import org.scalingmq.route.server.handler.RequestHandler;
import org.scalingmq.route.server.handler.RequestHandlerFactory;

import java.util.ServiceLoader;

/**
 * route启动类
 * @author renyansong
 */
@Slf4j
public class ScalingMqRouteApplication {

    private static volatile boolean STOP = false;

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) {
        // 先加载配置文件
        ConfigParseUtil.getInstance().parse(RouteConfig.getInstance());
        log.debug("当前路由系统配置:{}", RouteConfig.getInstance());

        // 启动所有组件
        ServiceLoader<Lifecycle> serviceLoader  = ServiceLoader.load(Lifecycle.class);
        for (Lifecycle lifecycle : serviceLoader) {
            IocContainer.getInstance().add(lifecycle);
        }
        for (Lifecycle lifecycle : serviceLoader) {
            lifecycle.componentStart();
        }

        // api handler init
        ServiceLoader<RequestHandler> requestHandlers = ServiceLoader.load(RequestHandler.class);
        for (RequestHandler requestHandler : requestHandlers) {
            RequestHandlerFactory.getInstance().addHandler(requestHandler);
        }

        // shutdown 钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Lifecycle lifecycle : serviceLoader) {
                lifecycle.componentStop();
            }
            STOP = true;
        }));

        // 挂起主线程
        while (!STOP) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

}
