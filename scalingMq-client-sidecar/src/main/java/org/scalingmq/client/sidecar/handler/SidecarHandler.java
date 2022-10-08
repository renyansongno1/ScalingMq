package org.scalingmq.client.sidecar.handler;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.client.grpc.ProduceReqWrapper;
import org.scalingmq.client.grpc.ProduceResWrapper;
import org.scalingmq.client.grpc.ScalingmqServiceGrpc;

/**
 * grpc handler
 * @author renyansong
 */
@Slf4j
public class SidecarHandler extends ScalingmqServiceGrpc.ScalingmqServiceImplBase {

    private static final String BROKER_HOST = "BROKER_HOST";

    private static final String BROKER_PORT = "BROKER_PORT";

    private final ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(BROKER_HOST, Integer.parseInt(BROKER_PORT))
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext();

    ScalingmqServiceGrpc.ScalingmqServiceBlockingStub scalingmqServiceBlockingStub
            = ScalingmqServiceGrpc.newBlockingStub(channelBuilder.build());

    @Override
    public void produce(ProduceReqWrapper.ProduceMsgReq request, StreamObserver<ProduceResWrapper.ProduceMsgRes> responseObserver) {
        if (log.isDebugEnabled()) {
            log.debug("sidecar收到消息:{}", request);
        }

        // 访问远端broker
        ProduceResWrapper.ProduceMsgRes msgRes = scalingmqServiceBlockingStub.produce(request);
        responseObserver.onNext(msgRes);
        responseObserver.onCompleted();
    }
}
