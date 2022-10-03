package org.scalingmq.demo.producer;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.demo.grpc.ProduceReqWrapper;
import org.scalingmq.demo.grpc.ProduceResWrapper;
import org.scalingmq.demo.grpc.ScalingmqServiceGrpc;
import org.scalingmq.demo.server.EndpointProcessor;
import org.scalingmq.demo.server.HttpEndpoint;
import org.scalingmq.demo.server.RequestBody;

import java.nio.charset.StandardCharsets;

/**
 * 生产者demo
 * @author renyansong
 */
@Slf4j
public class ProducerDemo implements EndpointProcessor {

    private static final String BROKER_HOST = System.getProperty("BROKER_HOST");

    private static final String BROKER_PORT = System.getProperty("BROKER_PORT");

    private final ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(BROKER_HOST, Integer.parseInt(BROKER_PORT))
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext();

    ScalingmqServiceGrpc.ScalingmqServiceBlockingStub scalingmqServiceBlockingStub
            = ScalingmqServiceGrpc.newBlockingStub(channelBuilder.build());

    @HttpEndpoint(value = "/produce")
    public String produce(@RequestBody ProduceMsg produceMsg) {
        ProduceReqWrapper.ProduceMsgReq req
                = ProduceReqWrapper.ProduceMsgReq.newBuilder()
                .setMessage(ByteString.copyFrom(produceMsg.getMsg().getBytes(StandardCharsets.UTF_8)))
                .setTopic(produceMsg.getTopic())
                .setStorageMsgWhenFail(true)
                .build();

        ProduceResWrapper.ProduceMsgRes produceMsgRes = scalingmqServiceBlockingStub.produce(req);
        log.info("发送消息响应:{}", produceMsgRes);
        return "ok";
    }

}