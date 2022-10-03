package org.scalingmq.client.sidecar.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.client.sidecar.handler.SidecarHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * grpc server端
 * @author renyansong
 */
@Slf4j
public class GrpcNettyServer {

    private Server server;

    public void start() {
        server = ServerBuilder.forPort(9999)
                .addService(new SidecarHandler())
                .build();
        try {
            server.start();
        } catch (IOException e) {
            log.error("grpc server启动异常", e);
        }
    }

    public void stop() {
        if (server != null) {
            try {
                server.shutdown().awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

}
