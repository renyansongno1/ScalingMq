package org.scalingmq.client.sidecar.handler;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.client.sidecar.grpc.ProduceReqWrapper;
import org.scalingmq.client.sidecar.grpc.ProduceResWrapper;
import org.scalingmq.client.sidecar.grpc.ScalingmqServiceGrpc;

/**
 * grpc handler
 * @author renyansong
 */
@Slf4j
public class SidecarHandler extends ScalingmqServiceGrpc.ScalingmqServiceImplBase {

    @Override
    public void produce(ProduceReqWrapper.ProduceMsgReq request, StreamObserver<ProduceResWrapper.ProduceMsgRes> responseObserver) {
        if (log.isDebugEnabled()) {
            log.debug("sidecar收到消息:{}", request);
        }

        ProduceResWrapper.ProduceMsgRes res = ProduceResWrapper.ProduceMsgRes.newBuilder()
                .setOffset(1)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }
}
