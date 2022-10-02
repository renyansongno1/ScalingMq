package org.scalingmq.broker.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.conf.BrokerConfig;
import org.scalingmq.broker.server.handler.BrokerGrpcHandler;
import org.scalingmq.common.lifecycle.Lifecycle;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * grpc server端
 * @author renyansong
 */
@Slf4j
public class GrpcNettyServer implements Lifecycle {

    private Server server;

    private void start() {
        server = ServerBuilder.forPort(BrokerConfig.MSG_PORT)
                .addService(new BrokerGrpcHandler())
                .build();
        try {
            server.start();
        } catch (IOException e) {
            log.error("grpc server启动异常", e);
        }
    }

    @Override
    public void componentStart() {
        start();
    }

    @Override
    public void componentStop() {
        if (server != null) {
            try {
                server.shutdown().awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

}
