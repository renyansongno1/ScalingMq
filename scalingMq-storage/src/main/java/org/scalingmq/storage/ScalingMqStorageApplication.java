package org.scalingmq.storage;

import org.scalingmq.storage.lifecycle.Lifecycle;

import java.util.ServiceLoader;

/**
 * 存储组件启动类
 * @author renyansong
 */
public class ScalingMqStorageApplication {

    private static volatile boolean STOP = false;

    public static void main(String[] args) {
        ServiceLoader<Lifecycle> serviceLoader  = ServiceLoader.load(Lifecycle.class);
        for (Lifecycle lifecycle : serviceLoader) {
            lifecycle.componentStart();
        }

        // shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Lifecycle lifecycle : serviceLoader) {
                lifecycle.componentStop();
            }
            STOP = true;
        }));

        while (!STOP) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // ignore
            }
        }

    }

}
