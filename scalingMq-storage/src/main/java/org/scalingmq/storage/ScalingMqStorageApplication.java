package org.scalingmq.storage;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.config.ConfigParseUtil;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.common.lifecycle.Lifecycle;

import java.util.ServiceLoader;

/**
 * 存储组件启动类
 * @author renyansong
 */
@Slf4j
public class ScalingMqStorageApplication {

    private static volatile boolean STOP = false;

    public static void main(String[] args) {
        // 先加载配置文件
        ConfigParseUtil.getInstance().parse(StorageConfig.getInstance());
        log.debug("当前存储系统配置:{}", StorageConfig.getInstance());
        // 启动所有组件
        ServiceLoader<Lifecycle> serviceLoader  = ServiceLoader.load(Lifecycle.class);
        for (Lifecycle lifecycle : serviceLoader) {
            lifecycle.componentStart();
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
