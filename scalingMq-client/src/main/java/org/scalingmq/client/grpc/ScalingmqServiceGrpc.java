package org.scalingmq.client.grpc;

import javax.annotation.processing.Generated;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: ScalingMqClient.proto")
public final class ScalingmqServiceGrpc {

  private ScalingmqServiceGrpc() {}

  public static final String SERVICE_NAME = "ScalingmqService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<ProduceReqWrapper.ProduceMsgReq,
      ProduceResWrapper.ProduceMsgRes> METHOD_PRODUCE =
      io.grpc.MethodDescriptor.<ProduceReqWrapper.ProduceMsgReq, ProduceResWrapper.ProduceMsgRes>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "ScalingmqService", "Produce"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              ProduceReqWrapper.ProduceMsgReq.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              ProduceResWrapper.ProduceMsgRes.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ScalingmqServiceStub newStub(io.grpc.Channel channel) {
    return new ScalingmqServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ScalingmqServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ScalingmqServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ScalingmqServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ScalingmqServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class ScalingmqServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * 生产消息
     * </pre>
     */
    public void produce(ProduceReqWrapper.ProduceMsgReq request,
        io.grpc.stub.StreamObserver<ProduceResWrapper.ProduceMsgRes> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_PRODUCE, responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_PRODUCE,
            asyncUnaryCall(
              new MethodHandlers<
                ProduceReqWrapper.ProduceMsgReq,
                ProduceResWrapper.ProduceMsgRes>(
                  this, METHODID_PRODUCE)))
          .build();
    }
  }

  /**
   */
  public static final class ScalingmqServiceStub extends io.grpc.stub.AbstractStub<ScalingmqServiceStub> {
    private ScalingmqServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ScalingmqServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ScalingmqServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ScalingmqServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * 生产消息
     * </pre>
     */
    public void produce(ProduceReqWrapper.ProduceMsgReq request,
        io.grpc.stub.StreamObserver<ProduceResWrapper.ProduceMsgRes> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_PRODUCE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ScalingmqServiceBlockingStub extends io.grpc.stub.AbstractStub<ScalingmqServiceBlockingStub> {
    private ScalingmqServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ScalingmqServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ScalingmqServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ScalingmqServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 生产消息
     * </pre>
     */
    public ProduceResWrapper.ProduceMsgRes produce(ProduceReqWrapper.ProduceMsgReq request) {
      return blockingUnaryCall(
          getChannel(), METHOD_PRODUCE, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ScalingmqServiceFutureStub extends io.grpc.stub.AbstractStub<ScalingmqServiceFutureStub> {
    private ScalingmqServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ScalingmqServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ScalingmqServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ScalingmqServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 生产消息
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ProduceResWrapper.ProduceMsgRes> produce(
        ProduceReqWrapper.ProduceMsgReq request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_PRODUCE, getCallOptions()), request);
    }
  }

  private static final int METHODID_PRODUCE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ScalingmqServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ScalingmqServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PRODUCE:
          serviceImpl.produce((ProduceReqWrapper.ProduceMsgReq) request,
              (io.grpc.stub.StreamObserver<ProduceResWrapper.ProduceMsgRes>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class ScalingmqServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ProducerWrapper.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ScalingmqServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ScalingmqServiceDescriptorSupplier())
              .addMethod(METHOD_PRODUCE)
              .build();
        }
      }
    }
    return result;
  }
}
