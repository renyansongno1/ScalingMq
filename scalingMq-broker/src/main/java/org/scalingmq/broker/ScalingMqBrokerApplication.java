package org.scalingmq.broker;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.conf.BrokerConfig;
import org.scalingmq.broker.server.handler.BrokerHttpNetEventHandler;
import org.scalingmq.broker.server.http.EndpointProcessor;
import org.scalingmq.broker.server.http.HttpEndpoint;
import org.scalingmq.common.config.ConfigParseUtil;
import org.scalingmq.common.ioc.IocContainer;
import org.scalingmq.common.lifecycle.Lifecycle;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Broker的启动类
 * @author renyansong
 */
@Slf4j
public class ScalingMqBrokerApplication {

    private static volatile boolean STOP = false;

    public static void main(String[] args) {
        // 加载配置文件
        ConfigParseUtil.getInstance().parse(BrokerConfig.getInstance());
        log.debug("broker 加载的配置信息:{}", BrokerConfig.getInstance().toString());

        // 启动所有组件
        ServiceLoader<Lifecycle> serviceLoader  = ServiceLoader.load(Lifecycle.class);
        for (Lifecycle lifecycle : serviceLoader) {
            IocContainer.getInstance().add(lifecycle);
        }
        for (Lifecycle lifecycle : serviceLoader) {
            lifecycle.componentStart();
        }

        // 加载http的endpoint
        for (EndpointProcessor endpointProcessor : ServiceLoader.load(EndpointProcessor.class)) {
            // 注册endpoint
            Class<? extends EndpointProcessor> processorClass = endpointProcessor.getClass();
            for (Method declaredMethod : processorClass.getDeclaredMethods()) {
                HttpEndpoint declaredAnnotation = declaredMethod.getDeclaredAnnotation(HttpEndpoint.class);
                if (declaredAnnotation != null) {
                    Map<Method, Object> methodObjectMap = new HashMap<>(2);
                    methodObjectMap.put(declaredMethod, endpointProcessor);
                    BrokerHttpNetEventHandler.getInstance().addUrlMapping(declaredAnnotation.value(), methodObjectMap);
                    log.debug("加载 http endpoint, url:{}", declaredAnnotation.value());
                }
            }
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
