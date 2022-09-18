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
public final class ControllerGrpc {

  private ControllerGrpc() {}

  public static final String SERVICE_NAME = "csi.v1.Controller";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.CreateVolumeRequest,
      csi.v1.Csi.CreateVolumeResponse> METHOD_CREATE_VOLUME =
      io.grpc.MethodDescriptor.<csi.v1.Csi.CreateVolumeRequest, csi.v1.Csi.CreateVolumeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "CreateVolume"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.CreateVolumeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.CreateVolumeResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.DeleteVolumeRequest,
      csi.v1.Csi.DeleteVolumeResponse> METHOD_DELETE_VOLUME =
      io.grpc.MethodDescriptor.<csi.v1.Csi.DeleteVolumeRequest, csi.v1.Csi.DeleteVolumeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "DeleteVolume"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.DeleteVolumeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.DeleteVolumeResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.ControllerPublishVolumeRequest,
      csi.v1.Csi.ControllerPublishVolumeResponse> METHOD_CONTROLLER_PUBLISH_VOLUME =
      io.grpc.MethodDescriptor.<csi.v1.Csi.ControllerPublishVolumeRequest, csi.v1.Csi.ControllerPublishVolumeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "ControllerPublishVolume"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ControllerPublishVolumeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ControllerPublishVolumeResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.ControllerUnpublishVolumeRequest,
      csi.v1.Csi.ControllerUnpublishVolumeResponse> METHOD_CONTROLLER_UNPUBLISH_VOLUME =
      io.grpc.MethodDescriptor.<csi.v1.Csi.ControllerUnpublishVolumeRequest, csi.v1.Csi.ControllerUnpublishVolumeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "ControllerUnpublishVolume"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ControllerUnpublishVolumeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ControllerUnpublishVolumeResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.ValidateVolumeCapabilitiesRequest,
      csi.v1.Csi.ValidateVolumeCapabilitiesResponse> METHOD_VALIDATE_VOLUME_CAPABILITIES =
      io.grpc.MethodDescriptor.<csi.v1.Csi.ValidateVolumeCapabilitiesRequest, csi.v1.Csi.ValidateVolumeCapabilitiesResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "ValidateVolumeCapabilities"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ValidateVolumeCapabilitiesRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ValidateVolumeCapabilitiesResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.ListVolumesRequest,
      csi.v1.Csi.ListVolumesResponse> METHOD_LIST_VOLUMES =
      io.grpc.MethodDescriptor.<csi.v1.Csi.ListVolumesRequest, csi.v1.Csi.ListVolumesResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "ListVolumes"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ListVolumesRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ListVolumesResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.GetCapacityRequest,
      csi.v1.Csi.GetCapacityResponse> METHOD_GET_CAPACITY =
      io.grpc.MethodDescriptor.<csi.v1.Csi.GetCapacityRequest, csi.v1.Csi.GetCapacityResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "GetCapacity"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.GetCapacityRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.GetCapacityResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.ControllerGetCapabilitiesRequest,
      csi.v1.Csi.ControllerGetCapabilitiesResponse> METHOD_CONTROLLER_GET_CAPABILITIES =
      io.grpc.MethodDescriptor.<csi.v1.Csi.ControllerGetCapabilitiesRequest, csi.v1.Csi.ControllerGetCapabilitiesResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "ControllerGetCapabilities"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ControllerGetCapabilitiesRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ControllerGetCapabilitiesResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.CreateSnapshotRequest,
      csi.v1.Csi.CreateSnapshotResponse> METHOD_CREATE_SNAPSHOT =
      io.grpc.MethodDescriptor.<csi.v1.Csi.CreateSnapshotRequest, csi.v1.Csi.CreateSnapshotResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "CreateSnapshot"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.CreateSnapshotRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.CreateSnapshotResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.DeleteSnapshotRequest,
      csi.v1.Csi.DeleteSnapshotResponse> METHOD_DELETE_SNAPSHOT =
      io.grpc.MethodDescriptor.<csi.v1.Csi.DeleteSnapshotRequest, csi.v1.Csi.DeleteSnapshotResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "DeleteSnapshot"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.DeleteSnapshotRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.DeleteSnapshotResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.ListSnapshotsRequest,
      csi.v1.Csi.ListSnapshotsResponse> METHOD_LIST_SNAPSHOTS =
      io.grpc.MethodDescriptor.<csi.v1.Csi.ListSnapshotsRequest, csi.v1.Csi.ListSnapshotsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "ListSnapshots"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ListSnapshotsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ListSnapshotsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.ControllerExpandVolumeRequest,
      csi.v1.Csi.ControllerExpandVolumeResponse> METHOD_CONTROLLER_EXPAND_VOLUME =
      io.grpc.MethodDescriptor.<csi.v1.Csi.ControllerExpandVolumeRequest, csi.v1.Csi.ControllerExpandVolumeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "ControllerExpandVolume"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ControllerExpandVolumeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ControllerExpandVolumeResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<csi.v1.Csi.ControllerGetVolumeRequest,
      csi.v1.Csi.ControllerGetVolumeResponse> METHOD_CONTROLLER_GET_VOLUME =
      io.grpc.MethodDescriptor.<csi.v1.Csi.ControllerGetVolumeRequest, csi.v1.Csi.ControllerGetVolumeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "csi.v1.Controller", "ControllerGetVolume"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ControllerGetVolumeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              csi.v1.Csi.ControllerGetVolumeResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ControllerStub newStub(io.grpc.Channel channel) {
    return new ControllerStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ControllerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ControllerBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ControllerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ControllerFutureStub(channel);
  }

  /**
   */
  public static abstract class ControllerImplBase implements io.grpc.BindableService {

    /**
     */
    public void createVolume(csi.v1.Csi.CreateVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.CreateVolumeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CREATE_VOLUME, responseObserver);
    }

    /**
     */
    public void deleteVolume(csi.v1.Csi.DeleteVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.DeleteVolumeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_DELETE_VOLUME, responseObserver);
    }

    /**
     */
    public void controllerPublishVolume(csi.v1.Csi.ControllerPublishVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerPublishVolumeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CONTROLLER_PUBLISH_VOLUME, responseObserver);
    }

    /**
     */
    public void controllerUnpublishVolume(csi.v1.Csi.ControllerUnpublishVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerUnpublishVolumeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CONTROLLER_UNPUBLISH_VOLUME, responseObserver);
    }

    /**
     */
    public void validateVolumeCapabilities(csi.v1.Csi.ValidateVolumeCapabilitiesRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ValidateVolumeCapabilitiesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_VALIDATE_VOLUME_CAPABILITIES, responseObserver);
    }

    /**
     */
    public void listVolumes(csi.v1.Csi.ListVolumesRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ListVolumesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_LIST_VOLUMES, responseObserver);
    }

    /**
     */
    public void getCapacity(csi.v1.Csi.GetCapacityRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.GetCapacityResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_CAPACITY, responseObserver);
    }

    /**
     */
    public void controllerGetCapabilities(csi.v1.Csi.ControllerGetCapabilitiesRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerGetCapabilitiesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CONTROLLER_GET_CAPABILITIES, responseObserver);
    }

    /**
     */
    public void createSnapshot(csi.v1.Csi.CreateSnapshotRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.CreateSnapshotResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CREATE_SNAPSHOT, responseObserver);
    }

    /**
     */
    public void deleteSnapshot(csi.v1.Csi.DeleteSnapshotRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.DeleteSnapshotResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_DELETE_SNAPSHOT, responseObserver);
    }

    /**
     */
    public void listSnapshots(csi.v1.Csi.ListSnapshotsRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ListSnapshotsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_LIST_SNAPSHOTS, responseObserver);
    }

    /**
     */
    public void controllerExpandVolume(csi.v1.Csi.ControllerExpandVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerExpandVolumeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CONTROLLER_EXPAND_VOLUME, responseObserver);
    }

    /**
     */
    public void controllerGetVolume(csi.v1.Csi.ControllerGetVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerGetVolumeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CONTROLLER_GET_VOLUME, responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_CREATE_VOLUME,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.CreateVolumeRequest,
                csi.v1.Csi.CreateVolumeResponse>(
                  this, METHODID_CREATE_VOLUME)))
          .addMethod(
            METHOD_DELETE_VOLUME,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.DeleteVolumeRequest,
                csi.v1.Csi.DeleteVolumeResponse>(
                  this, METHODID_DELETE_VOLUME)))
          .addMethod(
            METHOD_CONTROLLER_PUBLISH_VOLUME,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.ControllerPublishVolumeRequest,
                csi.v1.Csi.ControllerPublishVolumeResponse>(
                  this, METHODID_CONTROLLER_PUBLISH_VOLUME)))
          .addMethod(
            METHOD_CONTROLLER_UNPUBLISH_VOLUME,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.ControllerUnpublishVolumeRequest,
                csi.v1.Csi.ControllerUnpublishVolumeResponse>(
                  this, METHODID_CONTROLLER_UNPUBLISH_VOLUME)))
          .addMethod(
            METHOD_VALIDATE_VOLUME_CAPABILITIES,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.ValidateVolumeCapabilitiesRequest,
                csi.v1.Csi.ValidateVolumeCapabilitiesResponse>(
                  this, METHODID_VALIDATE_VOLUME_CAPABILITIES)))
          .addMethod(
            METHOD_LIST_VOLUMES,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.ListVolumesRequest,
                csi.v1.Csi.ListVolumesResponse>(
                  this, METHODID_LIST_VOLUMES)))
          .addMethod(
            METHOD_GET_CAPACITY,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.GetCapacityRequest,
                csi.v1.Csi.GetCapacityResponse>(
                  this, METHODID_GET_CAPACITY)))
          .addMethod(
            METHOD_CONTROLLER_GET_CAPABILITIES,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.ControllerGetCapabilitiesRequest,
                csi.v1.Csi.ControllerGetCapabilitiesResponse>(
                  this, METHODID_CONTROLLER_GET_CAPABILITIES)))
          .addMethod(
            METHOD_CREATE_SNAPSHOT,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.CreateSnapshotRequest,
                csi.v1.Csi.CreateSnapshotResponse>(
                  this, METHODID_CREATE_SNAPSHOT)))
          .addMethod(
            METHOD_DELETE_SNAPSHOT,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.DeleteSnapshotRequest,
                csi.v1.Csi.DeleteSnapshotResponse>(
                  this, METHODID_DELETE_SNAPSHOT)))
          .addMethod(
            METHOD_LIST_SNAPSHOTS,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.ListSnapshotsRequest,
                csi.v1.Csi.ListSnapshotsResponse>(
                  this, METHODID_LIST_SNAPSHOTS)))
          .addMethod(
            METHOD_CONTROLLER_EXPAND_VOLUME,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.ControllerExpandVolumeRequest,
                csi.v1.Csi.ControllerExpandVolumeResponse>(
                  this, METHODID_CONTROLLER_EXPAND_VOLUME)))
          .addMethod(
            METHOD_CONTROLLER_GET_VOLUME,
            asyncUnaryCall(
              new MethodHandlers<
                csi.v1.Csi.ControllerGetVolumeRequest,
                csi.v1.Csi.ControllerGetVolumeResponse>(
                  this, METHODID_CONTROLLER_GET_VOLUME)))
          .build();
    }
  }

  /**
   */
  public static final class ControllerStub extends io.grpc.stub.AbstractStub<ControllerStub> {
    private ControllerStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ControllerStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ControllerStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ControllerStub(channel, callOptions);
    }

    /**
     */
    public void createVolume(csi.v1.Csi.CreateVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.CreateVolumeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CREATE_VOLUME, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteVolume(csi.v1.Csi.DeleteVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.DeleteVolumeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_DELETE_VOLUME, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void controllerPublishVolume(csi.v1.Csi.ControllerPublishVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerPublishVolumeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CONTROLLER_PUBLISH_VOLUME, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void controllerUnpublishVolume(csi.v1.Csi.ControllerUnpublishVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerUnpublishVolumeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CONTROLLER_UNPUBLISH_VOLUME, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void validateVolumeCapabilities(csi.v1.Csi.ValidateVolumeCapabilitiesRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ValidateVolumeCapabilitiesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_VALIDATE_VOLUME_CAPABILITIES, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listVolumes(csi.v1.Csi.ListVolumesRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ListVolumesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_LIST_VOLUMES, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getCapacity(csi.v1.Csi.GetCapacityRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.GetCapacityResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_CAPACITY, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void controllerGetCapabilities(csi.v1.Csi.ControllerGetCapabilitiesRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerGetCapabilitiesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CONTROLLER_GET_CAPABILITIES, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createSnapshot(csi.v1.Csi.CreateSnapshotRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.CreateSnapshotResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CREATE_SNAPSHOT, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteSnapshot(csi.v1.Csi.DeleteSnapshotRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.DeleteSnapshotResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_DELETE_SNAPSHOT, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listSnapshots(csi.v1.Csi.ListSnapshotsRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ListSnapshotsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_LIST_SNAPSHOTS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void controllerExpandVolume(csi.v1.Csi.ControllerExpandVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerExpandVolumeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CONTROLLER_EXPAND_VOLUME, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void controllerGetVolume(csi.v1.Csi.ControllerGetVolumeRequest request,
        io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerGetVolumeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CONTROLLER_GET_VOLUME, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ControllerBlockingStub extends io.grpc.stub.AbstractStub<ControllerBlockingStub> {
    private ControllerBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ControllerBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ControllerBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ControllerBlockingStub(channel, callOptions);
    }

    /**
     */
    public csi.v1.Csi.CreateVolumeResponse createVolume(csi.v1.Csi.CreateVolumeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CREATE_VOLUME, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.DeleteVolumeResponse deleteVolume(csi.v1.Csi.DeleteVolumeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_DELETE_VOLUME, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.ControllerPublishVolumeResponse controllerPublishVolume(csi.v1.Csi.ControllerPublishVolumeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CONTROLLER_PUBLISH_VOLUME, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.ControllerUnpublishVolumeResponse controllerUnpublishVolume(csi.v1.Csi.ControllerUnpublishVolumeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CONTROLLER_UNPUBLISH_VOLUME, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.ValidateVolumeCapabilitiesResponse validateVolumeCapabilities(csi.v1.Csi.ValidateVolumeCapabilitiesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_VALIDATE_VOLUME_CAPABILITIES, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.ListVolumesResponse listVolumes(csi.v1.Csi.ListVolumesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_LIST_VOLUMES, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.GetCapacityResponse getCapacity(csi.v1.Csi.GetCapacityRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_CAPACITY, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.ControllerGetCapabilitiesResponse controllerGetCapabilities(csi.v1.Csi.ControllerGetCapabilitiesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CONTROLLER_GET_CAPABILITIES, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.CreateSnapshotResponse createSnapshot(csi.v1.Csi.CreateSnapshotRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CREATE_SNAPSHOT, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.DeleteSnapshotResponse deleteSnapshot(csi.v1.Csi.DeleteSnapshotRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_DELETE_SNAPSHOT, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.ListSnapshotsResponse listSnapshots(csi.v1.Csi.ListSnapshotsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_LIST_SNAPSHOTS, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.ControllerExpandVolumeResponse controllerExpandVolume(csi.v1.Csi.ControllerExpandVolumeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CONTROLLER_EXPAND_VOLUME, getCallOptions(), request);
    }

    /**
     */
    public csi.v1.Csi.ControllerGetVolumeResponse controllerGetVolume(csi.v1.Csi.ControllerGetVolumeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CONTROLLER_GET_VOLUME, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ControllerFutureStub extends io.grpc.stub.AbstractStub<ControllerFutureStub> {
    private ControllerFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ControllerFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ControllerFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ControllerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.CreateVolumeResponse> createVolume(
        csi.v1.Csi.CreateVolumeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CREATE_VOLUME, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.DeleteVolumeResponse> deleteVolume(
        csi.v1.Csi.DeleteVolumeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_DELETE_VOLUME, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.ControllerPublishVolumeResponse> controllerPublishVolume(
        csi.v1.Csi.ControllerPublishVolumeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CONTROLLER_PUBLISH_VOLUME, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.ControllerUnpublishVolumeResponse> controllerUnpublishVolume(
        csi.v1.Csi.ControllerUnpublishVolumeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CONTROLLER_UNPUBLISH_VOLUME, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.ValidateVolumeCapabilitiesResponse> validateVolumeCapabilities(
        csi.v1.Csi.ValidateVolumeCapabilitiesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_VALIDATE_VOLUME_CAPABILITIES, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.ListVolumesResponse> listVolumes(
        csi.v1.Csi.ListVolumesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_LIST_VOLUMES, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.GetCapacityResponse> getCapacity(
        csi.v1.Csi.GetCapacityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_CAPACITY, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.ControllerGetCapabilitiesResponse> controllerGetCapabilities(
        csi.v1.Csi.ControllerGetCapabilitiesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CONTROLLER_GET_CAPABILITIES, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.CreateSnapshotResponse> createSnapshot(
        csi.v1.Csi.CreateSnapshotRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CREATE_SNAPSHOT, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.DeleteSnapshotResponse> deleteSnapshot(
        csi.v1.Csi.DeleteSnapshotRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_DELETE_SNAPSHOT, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.ListSnapshotsResponse> listSnapshots(
        csi.v1.Csi.ListSnapshotsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_LIST_SNAPSHOTS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.ControllerExpandVolumeResponse> controllerExpandVolume(
        csi.v1.Csi.ControllerExpandVolumeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CONTROLLER_EXPAND_VOLUME, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<csi.v1.Csi.ControllerGetVolumeResponse> controllerGetVolume(
        csi.v1.Csi.ControllerGetVolumeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CONTROLLER_GET_VOLUME, getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_VOLUME = 0;
  private static final int METHODID_DELETE_VOLUME = 1;
  private static final int METHODID_CONTROLLER_PUBLISH_VOLUME = 2;
  private static final int METHODID_CONTROLLER_UNPUBLISH_VOLUME = 3;
  private static final int METHODID_VALIDATE_VOLUME_CAPABILITIES = 4;
  private static final int METHODID_LIST_VOLUMES = 5;
  private static final int METHODID_GET_CAPACITY = 6;
  private static final int METHODID_CONTROLLER_GET_CAPABILITIES = 7;
  private static final int METHODID_CREATE_SNAPSHOT = 8;
  private static final int METHODID_DELETE_SNAPSHOT = 9;
  private static final int METHODID_LIST_SNAPSHOTS = 10;
  private static final int METHODID_CONTROLLER_EXPAND_VOLUME = 11;
  private static final int METHODID_CONTROLLER_GET_VOLUME = 12;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ControllerImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ControllerImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_VOLUME:
          serviceImpl.createVolume((csi.v1.Csi.CreateVolumeRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.CreateVolumeResponse>) responseObserver);
          break;
        case METHODID_DELETE_VOLUME:
          serviceImpl.deleteVolume((csi.v1.Csi.DeleteVolumeRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.DeleteVolumeResponse>) responseObserver);
          break;
        case METHODID_CONTROLLER_PUBLISH_VOLUME:
          serviceImpl.controllerPublishVolume((csi.v1.Csi.ControllerPublishVolumeRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerPublishVolumeResponse>) responseObserver);
          break;
        case METHODID_CONTROLLER_UNPUBLISH_VOLUME:
          serviceImpl.controllerUnpublishVolume((csi.v1.Csi.ControllerUnpublishVolumeRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerUnpublishVolumeResponse>) responseObserver);
          break;
        case METHODID_VALIDATE_VOLUME_CAPABILITIES:
          serviceImpl.validateVolumeCapabilities((csi.v1.Csi.ValidateVolumeCapabilitiesRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.ValidateVolumeCapabilitiesResponse>) responseObserver);
          break;
        case METHODID_LIST_VOLUMES:
          serviceImpl.listVolumes((csi.v1.Csi.ListVolumesRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.ListVolumesResponse>) responseObserver);
          break;
        case METHODID_GET_CAPACITY:
          serviceImpl.getCapacity((csi.v1.Csi.GetCapacityRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.GetCapacityResponse>) responseObserver);
          break;
        case METHODID_CONTROLLER_GET_CAPABILITIES:
          serviceImpl.controllerGetCapabilities((csi.v1.Csi.ControllerGetCapabilitiesRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerGetCapabilitiesResponse>) responseObserver);
          break;
        case METHODID_CREATE_SNAPSHOT:
          serviceImpl.createSnapshot((csi.v1.Csi.CreateSnapshotRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.CreateSnapshotResponse>) responseObserver);
          break;
        case METHODID_DELETE_SNAPSHOT:
          serviceImpl.deleteSnapshot((csi.v1.Csi.DeleteSnapshotRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.DeleteSnapshotResponse>) responseObserver);
          break;
        case METHODID_LIST_SNAPSHOTS:
          serviceImpl.listSnapshots((csi.v1.Csi.ListSnapshotsRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.ListSnapshotsResponse>) responseObserver);
          break;
        case METHODID_CONTROLLER_EXPAND_VOLUME:
          serviceImpl.controllerExpandVolume((csi.v1.Csi.ControllerExpandVolumeRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerExpandVolumeResponse>) responseObserver);
          break;
        case METHODID_CONTROLLER_GET_VOLUME:
          serviceImpl.controllerGetVolume((csi.v1.Csi.ControllerGetVolumeRequest) request,
              (io.grpc.stub.StreamObserver<csi.v1.Csi.ControllerGetVolumeResponse>) responseObserver);
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

  private static final class ControllerDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return csi.v1.Csi.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ControllerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ControllerDescriptorSupplier())
              .addMethod(METHOD_CREATE_VOLUME)
              .addMethod(METHOD_DELETE_VOLUME)
              .addMethod(METHOD_CONTROLLER_PUBLISH_VOLUME)
              .addMethod(METHOD_CONTROLLER_UNPUBLISH_VOLUME)
              .addMethod(METHOD_VALIDATE_VOLUME_CAPABILITIES)
              .addMethod(METHOD_LIST_VOLUMES)
              .addMethod(METHOD_GET_CAPACITY)
              .addMethod(METHOD_CONTROLLER_GET_CAPABILITIES)
              .addMethod(METHOD_CREATE_SNAPSHOT)
              .addMethod(METHOD_DELETE_SNAPSHOT)
              .addMethod(METHOD_LIST_SNAPSHOTS)
              .addMethod(METHOD_CONTROLLER_EXPAND_VOLUME)
              .addMethod(METHOD_CONTROLLER_GET_VOLUME)
              .build();
        }
      }
    }
    return result;
  }
}
