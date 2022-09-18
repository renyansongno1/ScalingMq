package org.scalingmq.storage.csi.csiserver;

import com.google.protobuf.BoolValue;
import csi.v1.Csi;
import grpc.ControllerGrpc;
import grpc.IdentityGrpc;
import grpc.NodeGrpc;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.scalingmq.storage.csi.config.StorageConfig;

import java.nio.file.Path;

/**
 * k8s CSI插件 服务端
 * @author renyansong
 */
public class CsiGrpcServer {

    private static final String PLUGIN_NAME = "org.scalingmq.storage";

    /**
     * 启动一个Server端 监听请求
     */
    public void start(String unixPath) throws Exception {
        Server server = NettyServerBuilder
                .forAddress(new DomainSocketAddress(Path.of(unixPath).toFile()))
                .channelType(generateChannelType())
                .workerEventLoopGroup(generateWorkGroup())
                .bossEventLoopGroup(generateBossGroup())
                .addService(new IdentityService())
                .addService(new ControllerService())
                .addService(new NodeService())
                .build();
        server.start();
    }

    /**
     * 按照平台创建boss group
     * @return boss group
     */
    private EventLoopGroup generateBossGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup(1, new DefaultThreadFactory("netty-boss"));

        } else {
            return new NioEventLoopGroup(1, new DefaultThreadFactory("netty-boss"));
        }
    }

    /**
     * 按照平台创建work group
     * @return work group
     */
    private EventLoopGroup generateWorkGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup(2, new DefaultThreadFactory("netty-worker"));

        } else {
            return new NioEventLoopGroup(2, new DefaultThreadFactory("netty-worker"));
        }
    }

    /**
     * 按照平台创建channel type
     * @return channel type
     */
    private Class<? extends ServerChannel> generateChannelType() {
        if (Epoll.isAvailable()) {
            return EpollServerSocketChannel.class;
        }
        return NioServerSocketChannel.class;
    }

    /**
     * 对于k8s来识别插件身份信息的接口实现
     */
    private static class IdentityService extends IdentityGrpc.IdentityImplBase {
        @Override
        public void getPluginInfo(Csi.GetPluginInfoRequest request, StreamObserver<Csi.GetPluginInfoResponse> responseObserver) {
            Csi.GetPluginInfoResponse response = Csi.GetPluginInfoResponse.getDefaultInstance();
            response.toBuilder()
                    .setName(PLUGIN_NAME)
                    .setVendorVersion(StorageConfig.getCliVersion())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        /**
         * 标识当前插件的能力 包含attach阶段
         */
        @Override
        public void getPluginCapabilities(Csi.GetPluginCapabilitiesRequest request,
                                          StreamObserver<Csi.GetPluginCapabilitiesResponse> responseObserver) {
            Csi.GetPluginCapabilitiesResponse.Builder response = Csi.GetPluginCapabilitiesResponse.getDefaultInstance().toBuilder();
            // 声明是一个Controller
            response.addCapabilities(
                    Csi.PluginCapability.newBuilder()
                            .setService(
                                    Csi.PluginCapability.Service.newBuilder()
                                            .setType(Csi.PluginCapability.Service.Type.CONTROLLER_SERVICE)
                            )
                            .build());
            // 当前存储是不是可扩展的
            response.addCapabilities(Csi.PluginCapability.newBuilder()
                    .setVolumeExpansion(
                            Csi.PluginCapability.VolumeExpansion.newBuilder()
                                    .setType(Csi.PluginCapability.VolumeExpansion.Type.ONLINE)
                    )
                    .build());
            // 声明不是所有的pod都可以访问 是有策略的 不是平等存储
            response.addCapabilities(
                    Csi.PluginCapability.newBuilder()
                            .setService(
                                    Csi.PluginCapability.Service.newBuilder()
                                            .setType(Csi.PluginCapability.Service.Type.VOLUME_ACCESSIBILITY_CONSTRAINTS)
                            )
                            .build());
        }

        @Override
        public void probe(Csi.ProbeRequest request, StreamObserver<Csi.ProbeResponse> responseObserver) {
            Csi.ProbeResponse.Builder builder = Csi.ProbeResponse.getDefaultInstance().toBuilder();
            Csi.ProbeResponse response = builder.setReady(BoolValue.newBuilder().setValue(true)).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    /**
     * 对于pv的一些能力控制
     */
    private static class ControllerService extends ControllerGrpc.ControllerImplBase {
        @Override
        public void createVolume(Csi.CreateVolumeRequest request, StreamObserver<Csi.CreateVolumeResponse> responseObserver) {
            super.createVolume(request, responseObserver);
        }

        @Override
        public void deleteVolume(Csi.DeleteVolumeRequest request, StreamObserver<Csi.DeleteVolumeResponse> responseObserver) {
            super.deleteVolume(request, responseObserver);
        }

        @Override
        public void controllerPublishVolume(Csi.ControllerPublishVolumeRequest request, StreamObserver<Csi.ControllerPublishVolumeResponse> responseObserver) {
            super.controllerPublishVolume(request, responseObserver);
        }

        @Override
        public void controllerUnpublishVolume(Csi.ControllerUnpublishVolumeRequest request, StreamObserver<Csi.ControllerUnpublishVolumeResponse> responseObserver) {
            super.controllerUnpublishVolume(request, responseObserver);
        }

        @Override
        public void validateVolumeCapabilities(Csi.ValidateVolumeCapabilitiesRequest request, StreamObserver<Csi.ValidateVolumeCapabilitiesResponse> responseObserver) {
            super.validateVolumeCapabilities(request, responseObserver);
        }

        @Override
        public void listVolumes(Csi.ListVolumesRequest request, StreamObserver<Csi.ListVolumesResponse> responseObserver) {
            super.listVolumes(request, responseObserver);
        }

        @Override
        public void getCapacity(Csi.GetCapacityRequest request, StreamObserver<Csi.GetCapacityResponse> responseObserver) {
            super.getCapacity(request, responseObserver);
        }

        @Override
        public void controllerGetCapabilities(Csi.ControllerGetCapabilitiesRequest request, StreamObserver<Csi.ControllerGetCapabilitiesResponse> responseObserver) {
            super.controllerGetCapabilities(request, responseObserver);
        }

        @Override
        public void createSnapshot(Csi.CreateSnapshotRequest request, StreamObserver<Csi.CreateSnapshotResponse> responseObserver) {
            super.createSnapshot(request, responseObserver);
        }

        @Override
        public void deleteSnapshot(Csi.DeleteSnapshotRequest request, StreamObserver<Csi.DeleteSnapshotResponse> responseObserver) {
            super.deleteSnapshot(request, responseObserver);
        }

        @Override
        public void listSnapshots(Csi.ListSnapshotsRequest request, StreamObserver<Csi.ListSnapshotsResponse> responseObserver) {
            super.listSnapshots(request, responseObserver);
        }

        @Override
        public void controllerExpandVolume(Csi.ControllerExpandVolumeRequest request, StreamObserver<Csi.ControllerExpandVolumeResponse> responseObserver) {
            super.controllerExpandVolume(request, responseObserver);
        }

        @Override
        public void controllerGetVolume(Csi.ControllerGetVolumeRequest request, StreamObserver<Csi.ControllerGetVolumeResponse> responseObserver) {
            super.controllerGetVolume(request, responseObserver);
        }
    }

    /**
     * 对于节点的能力
     */
    private static class NodeService extends NodeGrpc.NodeImplBase {
        @Override
        public void nodeStageVolume(Csi.NodeStageVolumeRequest request, StreamObserver<Csi.NodeStageVolumeResponse> responseObserver) {
            super.nodeStageVolume(request, responseObserver);
        }

        @Override
        public void nodeUnstageVolume(Csi.NodeUnstageVolumeRequest request, StreamObserver<Csi.NodeUnstageVolumeResponse> responseObserver) {
            super.nodeUnstageVolume(request, responseObserver);
        }

        @Override
        public void nodePublishVolume(Csi.NodePublishVolumeRequest request, StreamObserver<Csi.NodePublishVolumeResponse> responseObserver) {
            super.nodePublishVolume(request, responseObserver);
        }

        @Override
        public void nodeUnpublishVolume(Csi.NodeUnpublishVolumeRequest request, StreamObserver<Csi.NodeUnpublishVolumeResponse> responseObserver) {
            super.nodeUnpublishVolume(request, responseObserver);
        }

        @Override
        public void nodeGetVolumeStats(Csi.NodeGetVolumeStatsRequest request, StreamObserver<Csi.NodeGetVolumeStatsResponse> responseObserver) {
            super.nodeGetVolumeStats(request, responseObserver);
        }

        @Override
        public void nodeExpandVolume(Csi.NodeExpandVolumeRequest request, StreamObserver<Csi.NodeExpandVolumeResponse> responseObserver) {
            super.nodeExpandVolume(request, responseObserver);
        }

        @Override
        public void nodeGetCapabilities(Csi.NodeGetCapabilitiesRequest request, StreamObserver<Csi.NodeGetCapabilitiesResponse> responseObserver) {
            super.nodeGetCapabilities(request, responseObserver);
        }

        @Override
        public void nodeGetInfo(Csi.NodeGetInfoRequest request, StreamObserver<Csi.NodeGetInfoResponse> responseObserver) {
            super.nodeGetInfo(request, responseObserver);
        }
    }

}
