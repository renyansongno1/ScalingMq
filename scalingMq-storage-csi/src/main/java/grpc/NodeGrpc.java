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
public final class NodeGrpc {

  private NodeGrpc() {}

  public static final String SERVICE_NAME = "csi.v1.Node";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.NodeStageVolumeRequest,
      csi.v1.Csi.NodeStageVolumeResponse> METHOD_NODE_STAGE_VOLUME =
      io.grpc.MethodDescriptor.<csi.v1.Csi.NodeStageVolumeRequest, csi.v1.Csi.NodeStageVolumeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Node", "NodeStageVolume"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeStageVolumeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeStageVolumeResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.NodeUnstageVolumeRequest,
      csi.v1.Csi.NodeUnstageVolumeResponse> METHOD_NODE_UNSTAGE_VOLUME =
      io.grpc.MethodDescriptor.<csi.v1.Csi.NodeUnstageVolumeRequest, csi.v1.Csi.NodeUnstageVolumeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Node", "NodeUnstageVolume"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeUnstageVolumeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeUnstageVolumeResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.NodePublishVolumeRequest,
      csi.v1.Csi.NodePublishVolumeResponse> METHOD_NODE_PUBLISH_VOLUME =
      io.grpc.MethodDescriptor.<csi.v1.Csi.NodePublishVolumeRequest, csi.v1.Csi.NodePublishVolumeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Node", "NodePublishVolume"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodePublishVolumeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodePublishVolumeResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.NodeUnpublishVolumeRequest,
      csi.v1.Csi.NodeUnpublishVolumeResponse> METHOD_NODE_UNPUBLISH_VOLUME =
      io.grpc.MethodDescriptor.<csi.v1.Csi.NodeUnpublishVolumeRequest, csi.v1.Csi.NodeUnpublishVolumeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Node", "NodeUnpublishVolume"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeUnpublishVolumeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeUnpublishVolumeResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.NodeGetVolumeStatsRequest,
      csi.v1.Csi.NodeGetVolumeStatsResponse> METHOD_NODE_GET_VOLUME_STATS =
      io.grpc.MethodDescriptor.<csi.v1.Csi.NodeGetVolumeStatsRequest, csi.v1.Csi.NodeGetVolumeStatsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Node", "NodeGetVolumeStats"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeGetVolumeStatsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeGetVolumeStatsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.NodeExpandVolumeRequest,
      csi.v1.Csi.NodeExpandVolumeResponse> METHOD_NODE_EXPAND_VOLUME =
      io.grpc.MethodDescriptor.<csi.v1.Csi.NodeExpandVolumeRequest, csi.v1.Csi.NodeExpandVolumeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Node", "NodeExpandVolume"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeExpandVolumeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeExpandVolumeResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.NodeGetCapabilitiesRequest,
      csi.v1.Csi.NodeGetCapabilitiesResponse> METHOD_NODE_GET_CAPABILITIES =
      io.grpc.MethodDescriptor.<csi.v1.Csi.NodeGetCapabilitiesRequest, csi.v1.Csi.NodeGetCapabilitiesResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Node", "NodeGetCapabilities"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeGetCapabilitiesRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeGetCapabilitiesResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.NodeGetInfoRequest,
      csi.v1.Csi.NodeGetInfoResponse> METHOD_NODE_GET_INFO =
      io.grpc.MethodDescriptor.<csi.v1.Csi.NodeGetInfoRequest, csi.v1.Csi.NodeGetInfoResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Node", "NodeGetInfo"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeGetInfoRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.NodeGetInfoResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NodeStub newStub(io.grpc.Channel channel) {
    return new NodeStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NodeBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new NodeBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NodeFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new NodeFutureStub(channel);
  }

  /**
   */
  public static abstract class NodeImplBase implements io.grpc.BindableService {

    /**
     */
    public void nodeStageVolume(csi.v1.Csi.NodeStageVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeStageVolumeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_NODE_STAGE_VOLUME, responseObserver);
    }

    /**
     */
    public void nodeUnstageVolume(csi.v1.Csi.NodeUnstageVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeUnstageVolumeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_NODE_UNSTAGE_VOLUME, responseObserver);
    }

    /**
     */
    public void nodePublishVolume(csi.v1.Csi.NodePublishVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodePublishVolumeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_NODE_PUBLISH_VOLUME, responseObserver);
    }

    /**
     */
    public void nodeUnpublishVolume(csi.v1.Csi.NodeUnpublishVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeUnpublishVolumeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_NODE_UNPUBLISH_VOLUME, responseObserver);
    }

    /**
     */
    public void nodeGetVolumeStats(csi.v1.Csi.NodeGetVolumeStatsRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeGetVolumeStatsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_NODE_GET_VOLUME_STATS, responseObserver);
    }

    /**
     */
    public void nodeExpandVolume(csi.v1.Csi.NodeExpandVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeExpandVolumeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_NODE_EXPAND_VOLUME, responseObserver);
    }

    /**
     */
    public void nodeGetCapabilities(csi.v1.Csi.NodeGetCapabilitiesRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeGetCapabilitiesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_NODE_GET_CAPABILITIES, responseObserver);
    }

    /**
     */
    public void nodeGetInfo(csi.v1.Csi.NodeGetInfoRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeGetInfoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_NODE_GET_INFO, responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_NODE_STAGE_VOLUME,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.NodeStageVolumeRequest,
                csi.v1.Csi.NodeStageVolumeResponse>(
                  this, METHODID_NODE_STAGE_VOLUME)))
          .addMethod(
            METHOD_NODE_UNSTAGE_VOLUME,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.NodeUnstageVolumeRequest,
                csi.v1.Csi.NodeUnstageVolumeResponse>(
                  this, METHODID_NODE_UNSTAGE_VOLUME)))
          .addMethod(
            METHOD_NODE_PUBLISH_VOLUME,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.NodePublishVolumeRequest,
                csi.v1.Csi.NodePublishVolumeResponse>(
                  this, METHODID_NODE_PUBLISH_VOLUME)))
          .addMethod(
            METHOD_NODE_UNPUBLISH_VOLUME,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.NodeUnpublishVolumeRequest,
                csi.v1.Csi.NodeUnpublishVolumeResponse>(
                  this, METHODID_NODE_UNPUBLISH_VOLUME)))
          .addMethod(
            METHOD_NODE_GET_VOLUME_STATS,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.NodeGetVolumeStatsRequest,
                csi.v1.Csi.NodeGetVolumeStatsResponse>(
                  this, METHODID_NODE_GET_VOLUME_STATS)))
          .addMethod(
            METHOD_NODE_EXPAND_VOLUME,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.NodeExpandVolumeRequest,
                csi.v1.Csi.NodeExpandVolumeResponse>(
                  this, METHODID_NODE_EXPAND_VOLUME)))
          .addMethod(
            METHOD_NODE_GET_CAPABILITIES,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.NodeGetCapabilitiesRequest,
                csi.v1.Csi.NodeGetCapabilitiesResponse>(
                  this, METHODID_NODE_GET_CAPABILITIES)))
          .addMethod(
            METHOD_NODE_GET_INFO,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.NodeGetInfoRequest,
                csi.v1.Csi.NodeGetInfoResponse>(
                  this, METHODID_NODE_GET_INFO)))
          .build();
    }
  }

  /**
   */
  public static final class NodeStub extends io.grpc.stub.AbstractStub<NodeStub> {
    private NodeStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected NodeStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeStub(channel, callOptions);
    }

    /**
     */
    public void nodeStageVolume(csi.v1.Csi.NodeStageVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeStageVolumeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_NODE_STAGE_VOLUME, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void nodeUnstageVolume(csi.v1.Csi.NodeUnstageVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeUnstageVolumeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_NODE_UNSTAGE_VOLUME, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void nodePublishVolume(csi.v1.Csi.NodePublishVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodePublishVolumeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_NODE_PUBLISH_VOLUME, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void nodeUnpublishVolume(csi.v1.Csi.NodeUnpublishVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeUnpublishVolumeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_NODE_UNPUBLISH_VOLUME, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void nodeGetVolumeStats(csi.v1.Csi.NodeGetVolumeStatsRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeGetVolumeStatsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_NODE_GET_VOLUME_STATS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void nodeExpandVolume(csi.v1.Csi.NodeExpandVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeExpandVolumeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_NODE_EXPAND_VOLUME, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void nodeGetCapabilities(csi.v1.Csi.NodeGetCapabilitiesRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeGetCapabilitiesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_NODE_GET_CAPABILITIES, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void nodeGetInfo(csi.v1.Csi.NodeGetInfoRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.NodeGetInfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_NODE_GET_INFO, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class NodeBlockingStub extends io.grpc.stub.AbstractStub<NodeBlockingStub> {
    private NodeBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected NodeBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeBlockingStub(channel, callOptions);
    }

    /**
     */
    public csi.v1.Csi.NodeStageVolumeResponse nodeStageVolume(csi.v1.Csi.NodeStageVolumeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_NODE_STAGE_VOLUME, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.NodeUnstageVolumeResponse nodeUnstageVolume(csi.v1.Csi.NodeUnstageVolumeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_NODE_UNSTAGE_VOLUME, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.NodePublishVolumeResponse nodePublishVolume(csi.v1.Csi.NodePublishVolumeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_NODE_PUBLISH_VOLUME, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.NodeUnpublishVolumeResponse nodeUnpublishVolume(csi.v1.Csi.NodeUnpublishVolumeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_NODE_UNPUBLISH_VOLUME, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.NodeGetVolumeStatsResponse nodeGetVolumeStats(csi.v1.Csi.NodeGetVolumeStatsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_NODE_GET_VOLUME_STATS, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.NodeExpandVolumeResponse nodeExpandVolume(csi.v1.Csi.NodeExpandVolumeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_NODE_EXPAND_VOLUME, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.NodeGetCapabilitiesResponse nodeGetCapabilities(csi.v1.Csi.NodeGetCapabilitiesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_NODE_GET_CAPABILITIES, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.NodeGetInfoResponse nodeGetInfo(csi.v1.Csi.NodeGetInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_NODE_GET_INFO, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class NodeFutureStub extends io.grpc.stub.AbstractStub<NodeFutureStub> {
    private NodeFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected NodeFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.NodeStageVolumeResponse> nodeStageVolume(
        csi.v1.Csi.NodeStageVolumeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_NODE_STAGE_VOLUME, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.NodeUnstageVolumeResponse> nodeUnstageVolume(
        csi.v1.Csi.NodeUnstageVolumeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_NODE_UNSTAGE_VOLUME, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.NodePublishVolumeResponse> nodePublishVolume(
        csi.v1.Csi.NodePublishVolumeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_NODE_PUBLISH_VOLUME, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.NodeUnpublishVolumeResponse> nodeUnpublishVolume(
        csi.v1.Csi.NodeUnpublishVolumeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_NODE_UNPUBLISH_VOLUME, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.NodeGetVolumeStatsResponse> nodeGetVolumeStats(
        csi.v1.Csi.NodeGetVolumeStatsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_NODE_GET_VOLUME_STATS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.NodeExpandVolumeResponse> nodeExpandVolume(
        csi.v1.Csi.NodeExpandVolumeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_NODE_EXPAND_VOLUME, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.NodeGetCapabilitiesResponse> nodeGetCapabilities(
        csi.v1.Csi.NodeGetCapabilitiesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_NODE_GET_CAPABILITIES, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.NodeGetInfoResponse> nodeGetInfo(
        csi.v1.Csi.NodeGetInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_NODE_GET_INFO, getCallOptions()), request);
    }
  }

  private static final int METHODID_NODE_STAGE_VOLUME = 0;
  private static final int METHODID_NODE_UNSTAGE_VOLUME = 1;
  private static final int METHODID_NODE_PUBLISH_VOLUME = 2;
  private static final int METHODID_NODE_UNPUBLISH_VOLUME = 3;
  private static final int METHODID_NODE_GET_VOLUME_STATS = 4;
  private static final int METHODID_NODE_EXPAND_VOLUME = 5;
  private static final int METHODID_NODE_GET_CAPABILITIES = 6;
  private static final int METHODID_NODE_GET_INFO = 7;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final NodeImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(NodeImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_NODE_STAGE_VOLUME:
          serviceImpl.nodeStageVolume((csi.v1.Csi.NodeStageVolumeRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.NodeStageVolumeResponse>) responseObserver);
          break;
        case METHODID_NODE_UNSTAGE_VOLUME:
          serviceImpl.nodeUnstageVolume((csi.v1.Csi.NodeUnstageVolumeRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.NodeUnstageVolumeResponse>) responseObserver);
          break;
        case METHODID_NODE_PUBLISH_VOLUME:
          serviceImpl.nodePublishVolume((csi.v1.Csi.NodePublishVolumeRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.NodePublishVolumeResponse>) responseObserver);
          break;
        case METHODID_NODE_UNPUBLISH_VOLUME:
          serviceImpl.nodeUnpublishVolume((csi.v1.Csi.NodeUnpublishVolumeRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.NodeUnpublishVolumeResponse>) responseObserver);
          break;
        case METHODID_NODE_GET_VOLUME_STATS:
          serviceImpl.nodeGetVolumeStats((csi.v1.Csi.NodeGetVolumeStatsRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.NodeGetVolumeStatsResponse>) responseObserver);
          break;
        case METHODID_NODE_EXPAND_VOLUME:
          serviceImpl.nodeExpandVolume((csi.v1.Csi.NodeExpandVolumeRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.NodeExpandVolumeResponse>) responseObserver);
          break;
        case METHODID_NODE_GET_CAPABILITIES:
          serviceImpl.nodeGetCapabilities((csi.v1.Csi.NodeGetCapabilitiesRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.NodeGetCapabilitiesResponse>) responseObserver);
          break;
        case METHODID_NODE_GET_INFO:
          serviceImpl.nodeGetInfo((csi.v1.Csi.NodeGetInfoRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.NodeGetInfoResponse>) responseObserver);
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

  private static final class NodeDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return csi.v1.Csi.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (NodeGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new NodeDescriptorSupplier())
              .addMethod(METHOD_NODE_STAGE_VOLUME)
              .addMethod(METHOD_NODE_UNSTAGE_VOLUME)
              .addMethod(METHOD_NODE_PUBLISH_VOLUME)
              .addMethod(METHOD_NODE_UNPUBLISH_VOLUME)
              .addMethod(METHOD_NODE_GET_VOLUME_STATS)
              .addMethod(METHOD_NODE_EXPAND_VOLUME)
              .addMethod(METHOD_NODE_GET_CAPABILITIES)
              .addMethod(METHOD_NODE_GET_INFO)
              .build();
        }
      }
    }
    return result;
  }
}
