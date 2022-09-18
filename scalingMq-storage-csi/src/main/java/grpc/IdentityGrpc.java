package grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.processing.Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: csi.proto")
public final class IdentityGrpc {

  private IdentityGrpc() {}

  public static final String SERVICE_NAME = "csi.v1.Identity";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.GetPluginInfoRequest,
      csi.v1.Csi.GetPluginInfoResponse> METHOD_GET_PLUGIN_INFO =
      io.grpc.MethodDescriptor.<csi.v1.Csi.GetPluginInfoRequest, csi.v1.Csi.GetPluginInfoResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Identity", "GetPluginInfo"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.GetPluginInfoRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.GetPluginInfoResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.GetPluginCapabilitiesRequest,
      csi.v1.Csi.GetPluginCapabilitiesResponse> METHOD_GET_PLUGIN_CAPABILITIES =
      io.grpc.MethodDescriptor.<csi.v1.Csi.GetPluginCapabilitiesRequest, csi.v1.Csi.GetPluginCapabilitiesResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Identity", "GetPluginCapabilities"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.GetPluginCapabilitiesRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.GetPluginCapabilitiesResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.ProbeRequest,
      csi.v1.Csi.ProbeResponse> METHOD_PROBE =
      io.grpc.MethodDescriptor.<csi.v1.Csi.ProbeRequest, csi.v1.Csi.ProbeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Identity", "Probe"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ProbeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ProbeResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static IdentityStub newStub(io.grpc.Channel channel) {
    return new IdentityStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static IdentityBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new IdentityBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static IdentityFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new IdentityFutureStub(channel);
  }

  /**
   */
  public static abstract class IdentityImplBase implements io.grpc.BindableService {

    /**
     */
    public void getPluginInfo(csi.v1.Csi.GetPluginInfoRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.GetPluginInfoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_PLUGIN_INFO, responseObserver);
    }

    /**
     */
    public void getPluginCapabilities(csi.v1.Csi.GetPluginCapabilitiesRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.GetPluginCapabilitiesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_PLUGIN_CAPABILITIES, responseObserver);
    }

    /**
     */
    public void probe(csi.v1.Csi.ProbeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ProbeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_PROBE, responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_GET_PLUGIN_INFO,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.GetPluginInfoRequest,
                csi.v1.Csi.GetPluginInfoResponse>(
                  this, METHODID_GET_PLUGIN_INFO)))
          .addMethod(
            METHOD_GET_PLUGIN_CAPABILITIES,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.GetPluginCapabilitiesRequest,
                csi.v1.Csi.GetPluginCapabilitiesResponse>(
                  this, METHODID_GET_PLUGIN_CAPABILITIES)))
          .addMethod(
            METHOD_PROBE,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.ProbeRequest,
                csi.v1.Csi.ProbeResponse>(
                  this, METHODID_PROBE)))
          .build();
    }
  }

  /**
   */
  public static final class IdentityStub extends io.grpc.stub.AbstractStub<IdentityStub> {
    private IdentityStub(io.grpc.Channel channel) {
      super(channel);
    }

    private IdentityStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected IdentityStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new IdentityStub(channel, callOptions);
    }

    /**
     */
    public void getPluginInfo(csi.v1.Csi.GetPluginInfoRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.GetPluginInfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_PLUGIN_INFO, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getPluginCapabilities(csi.v1.Csi.GetPluginCapabilitiesRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.GetPluginCapabilitiesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_PLUGIN_CAPABILITIES, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void probe(csi.v1.Csi.ProbeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ProbeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_PROBE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class IdentityBlockingStub extends io.grpc.stub.AbstractStub<IdentityBlockingStub> {
    private IdentityBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private IdentityBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected IdentityBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new IdentityBlockingStub(channel, callOptions);
    }

    /**
     */
    public csi.v1.Csi.GetPluginInfoResponse getPluginInfo(csi.v1.Csi.GetPluginInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_PLUGIN_INFO, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.GetPluginCapabilitiesResponse getPluginCapabilities(csi.v1.Csi.GetPluginCapabilitiesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_PLUGIN_CAPABILITIES, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.ProbeResponse probe(csi.v1.Csi.ProbeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_PROBE, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class IdentityFutureStub extends io.grpc.stub.AbstractStub<IdentityFutureStub> {
    private IdentityFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private IdentityFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected IdentityFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new IdentityFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.GetPluginInfoResponse> getPluginInfo(
        csi.v1.Csi.GetPluginInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_PLUGIN_INFO, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.GetPluginCapabilitiesResponse> getPluginCapabilities(
        csi.v1.Csi.GetPluginCapabilitiesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_PLUGIN_CAPABILITIES, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.ProbeResponse> probe(
        csi.v1.Csi.ProbeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_PROBE, getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_PLUGIN_INFO = 0;
  private static final int METHODID_GET_PLUGIN_CAPABILITIES = 1;
  private static final int METHODID_PROBE = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final IdentityImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(IdentityImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_PLUGIN_INFO:
          serviceImpl.getPluginInfo((csi.v1.Csi.GetPluginInfoRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.GetPluginInfoResponse>) responseObserver);
          break;
        case METHODID_GET_PLUGIN_CAPABILITIES:
          serviceImpl.getPluginCapabilities((csi.v1.Csi.GetPluginCapabilitiesRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.GetPluginCapabilitiesResponse>) responseObserver);
          break;
        case METHODID_PROBE:
          serviceImpl.probe((csi.v1.Csi.ProbeRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.ProbeResponse>) responseObserver);
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

  private static final class IdentityDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return csi.v1.Csi.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (IdentityGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new IdentityDescriptorSupplier())
              .addMethod(METHOD_GET_PLUGIN_INFO)
              .addMethod(METHOD_GET_PLUGIN_CAPABILITIES)
              .addMethod(METHOD_PROBE)
              .build();
        }
      }
    }
    return result;
  }
}
