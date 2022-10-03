package org.scalingmq.client.sidecar;

import org.scalingmq.client.sidecar.iptable.NatIptableManager;
import org.scalingmq.client.sidecar.server.GrpcNettyServer;

/**
 * client的sidecar启动类
 * @author renyansong
 */
public class ScalingMqClientSidecarApplication {

    private static volatile boolean STOP = false;

    public static void main(String[] args) {
        // 创建iptable规则
        NatIptableManager.getInstance().init();

        // 启动grpc客户端
        GrpcNettyServer grpcNettyServer = new GrpcNettyServer();
        grpcNettyServer.start();

        // shutdown 钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            grpcNettyServer.stop();
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
