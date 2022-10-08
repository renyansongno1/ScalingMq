package org.scalingmq.broker.server.handler;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.server.grpc.ProduceReqWrapper;
import org.scalingmq.broker.server.grpc.ProduceResWrapper;
import org.scalingmq.broker.server.grpc.ScalingmqServiceGrpc;

/**
 * grpc的代理实现
 * @author renyansong
 */
@Slf4j
public class BrokerGrpcHandler extends ScalingmqServiceGrpc.ScalingmqServiceImplBase {

    /**
     * 接收发送消息的请求
     * @param request 发送消息的请求
     * @param responseObserver 回执
     */
    @Override
    public void produce(ProduceReqWrapper.ProduceMsgReq request,
                        StreamObserver<ProduceResWrapper.ProduceMsgRes> responseObserver) {
        if (log.isDebugEnabled()) {
            log.debug("收到请求:{}", request);
        }

        ProduceResWrapper.ProduceMsgRes res = ProduceResWrapper.ProduceMsgRes.newBuilder()
                .setOffset(1)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

}
