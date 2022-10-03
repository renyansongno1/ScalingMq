package org.scalingmq.demo;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.demo.server.EndpointProcessor;
import org.scalingmq.demo.server.HttpEndpoint;
import org.scalingmq.demo.server.HttpNetEventHandler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * demo 启动类
 * @author renyansong
 */
@Slf4j
public class DemoApplication {

    private static volatile boolean STOP = false;

    public static void main(String[] args) {
        // 加载http的endpoint
        for (EndpointProcessor endpointProcessor : ServiceLoader.load(EndpointProcessor.class)) {
            // 注册endpoint
            Class<? extends EndpointProcessor> processorClass = endpointProcessor.getClass();
            for (Method declaredMethod : processorClass.getDeclaredMethods()) {
                HttpEndpoint declaredAnnotation = declaredMethod.getDeclaredAnnotation(HttpEndpoint.class);
                if (declaredAnnotation != null) {
                    Map<Method, Object> methodObjectMap = new HashMap<>(2);
                    methodObjectMap.put(declaredMethod, endpointProcessor);
                    HttpNetEventHandler.getInstance().addUrlMapping(declaredAnnotation.value(), methodObjectMap);
                    log.debug("加载 http endpoint, url:{}", declaredAnnotation.value());
                }
            }
        }

        // shutdown 钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
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
