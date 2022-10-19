package org.scalingmq.storage.csi.csiserver;

import com.google.gson.Gson;
import com.google.protobuf.BoolValue;
import csi.v1.Csi;
import grpc.ControllerGrpc;
import grpc.IdentityGrpc;
import grpc.NodeGrpc;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.kqueue.KQueueServerDomainSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.storage.csi.config.StorageCsiConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

/**
 * k8s CSI插件 服务端
 * @author renyansong
 */
@Slf4j
public class CsiGrpcServer {

    private static final String METADATA_PATH = "/metadata";

    private static final String METADATA_FILE_PATH = METADATA_PATH + "/storage-csi-metadata.json";

    private static final Gson GSON = new Gson();

    private Server server;

    /**
     * 启动一个Server端 监听请求
     */
    public void start(String unixPath) throws Exception {
        // 启动加载数据
        Path metadataPath = Path.of(METADATA_PATH);
        if (!Files.exists(metadataPath)) {
            metadataPath.toFile().mkdirs();
            Path jsonFilePath = Path.of(METADATA_FILE_PATH);
            if (!Files.exists(jsonFilePath)) {
                Files.createFile(jsonFilePath);
            }
        } else {
            // 读取数据
            String jsonStr = Files.readString(Path.of(METADATA_FILE_PATH));
            if (jsonStr != null && !"".equals(jsonStr)) {
                Metadata metadata = GSON.fromJson(jsonStr, Metadata.class);
                if (metadata.getNodeId() != null && !"".equals(metadata.getNodeId())) {
                    Metadata.getInstance().setNodeId(metadata.getNodeId());
                }
                Map<String, VolumeEntry> pvcVolumeRelation = Metadata.getInstance().getPvcVolumeRelation();
                pvcVolumeRelation.putAll(metadata.getPvcVolumeRelation());
            }
        }
        Path path = Path.of(unixPath);
        if (!Files.exists(path)) {
            String[] split = unixPath.split("/");
            String directory = unixPath.replaceAll(split[split.length-1],  "");
            boolean mkdirs = Path.of(directory).toFile().mkdirs();
            if (mkdirs) {
                Files.createFile(path);
            }
        }
        server = NettyServerBuilder
                .forAddress(new DomainSocketAddress(path.toFile()))
                .channelType(generateChannelType())
                .workerEventLoopGroup(generateWorkGroup())
                .bossEventLoopGroup(generateBossGroup())
                .addService(new IdentityService())
                .addService(new ControllerService())
                .addService(new NodeService())
                .directExecutor()
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
            return EpollServerDomainSocketChannel.class;
        }
        return KQueueServerDomainSocketChannel.class;
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * 对于k8s来识别插件身份信息的接口实现
     */
    private static class IdentityService extends IdentityGrpc.IdentityImplBase {
        @Override
        public void getPluginInfo(Csi.GetPluginInfoRequest request, StreamObserver<Csi.GetPluginInfoResponse> responseObserver) {
            log.info("收到plugin info请求:{}", request);
            Csi.GetPluginInfoResponse response = Csi.GetPluginInfoResponse.getDefaultInstance();
            Csi.GetPluginInfoResponse.Builder builder = response.toBuilder();
            builder.setName(StorageCsiConfig.CSI_PLUGIN_NAME)
                    .setVendorVersion(StorageCsiConfig.getCliVersion())
                    .build();
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }

        /**
         * 标识当前插件的能力 包含attach阶段
         */
        @Override
        public void getPluginCapabilities(Csi.GetPluginCapabilitiesRequest request,
                                          StreamObserver<Csi.GetPluginCapabilitiesResponse> responseObserver) {
            log.info("收到 plugin capabilities请求:{}", request);
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
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }

        @Override
        public void probe(Csi.ProbeRequest request, StreamObserver<Csi.ProbeResponse> responseObserver) {
            log.info("收到plugin probe请求:{}", request);
            Csi.ProbeResponse.Builder builder = Csi.ProbeResponse.getDefaultInstance().toBuilder();
            Csi.ProbeResponse response = builder.setReady(BoolValue.newBuilder().setValue(true)).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    /**
     * 对于pv的一些能力控制
     * 构建pv之后，会触发如何管理这些volume，还有扩容的时候
     */
    private static class ControllerService extends ControllerGrpc.ControllerImplBase {
        @SuppressWarnings("AlibabaSwitchStatement")
        @Override
        public void createVolume(Csi.CreateVolumeRequest request, StreamObserver<Csi.CreateVolumeResponse> responseObserver) {
            log.info("收到volume创建请求:{}", request);
            Csi.CreateVolumeResponse.Builder builder = Csi.CreateVolumeResponse.getDefaultInstance().toBuilder();

            VolumeEntry volumeEntry = Metadata.getInstance().getPvcVolumeRelation().get(request.getName());
            if (volumeEntry != null) {
                // 已经创建过了
                Csi.CreateVolumeResponse response = builder.setVolume(Csi.Volume.newBuilder()
                                .setVolumeId(volumeEntry.getVolumeId())
                                .setCapacityBytes(volumeEntry.getCapacityBytes())
                                .build())
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }

            // 获取当前环境
            StorageCsiConfig.CloudType cloudType = StorageCsiConfig.CloudType.valueOf(StorageCsiConfig.getInstance().getCloudEnv());
            switch (cloudType) {
                case ALI_YUN -> log.info("使用阿里云环境创建volume");
                case TENCENT_YUN -> log.info("使用腾讯云环境创建volume");
                case HUAWEI_YUN -> log.info("使用华为云环境创建volume");
                case LOCAL -> {
                    log.info("使用本地创建volume");
                    String volumeId = UUID.randomUUID().toString();
                    VolumeEntry storageVolumeEntry = new VolumeEntry();
                    storageVolumeEntry.setVolumeId(volumeId);
                    storageVolumeEntry.setCapacityBytes(request.getCapacityRange().getRequiredBytes());
                    // 保存在本地
                    Metadata.getInstance().getPvcVolumeRelation().put(request.getName(), storageVolumeEntry);
                    try {
                        Files.writeString(Path.of(METADATA_FILE_PATH), GSON.toJson(Metadata.getInstance()));
                    } catch (IOException e) {
                        log.error("写入元数据异常:{}", Metadata.getInstance(), e);
                    }
                    Csi.CreateVolumeResponse response = builder.setVolume(Csi.Volume.newBuilder()
                                    .setVolumeId(volumeId)
                                    .setCapacityBytes(storageVolumeEntry.getCapacityBytes())
                                    .addAccessibleTopology(
                                            Csi.Topology.newBuilder()
                                            .putSegments("local", storageVolumeEntry.toString())
                                            .build())
                                    .build())
                            .build();

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
                default -> {
                    log.error("不支持的集群环境");
                    responseObserver.onNext(null);
                    responseObserver.onCompleted();
                }
            }
        }

        @Override
        public void deleteVolume(Csi.DeleteVolumeRequest request, StreamObserver<Csi.DeleteVolumeResponse> responseObserver) {
            log.info("收到volume删除请求:{}", request);
            super.deleteVolume(request, responseObserver);
        }

        @SuppressWarnings("AlibabaSwitchStatement")
        @Override
        public void controllerPublishVolume(Csi.ControllerPublishVolumeRequest request, StreamObserver<Csi.ControllerPublishVolumeResponse> responseObserver) {
            log.info("收到volume publish请求, attach阶段:{}", request);
            boolean createdVolume = false;
            VolumeEntry publishVolume = null;
            for (VolumeEntry volumeEntry : Metadata.getInstance().getPvcVolumeRelation().values()) {
                if (volumeEntry.getVolumeId().equals(request.getVolumeId())) {
                    createdVolume = true;
                    publishVolume = volumeEntry;
                    break;
                }
            }
            if (!createdVolume) {
                log.error("publish阶段收到没有创建过的volume:{}", request);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("not created volume")
                        .asRuntimeException());
                return;
            }
            // 这个阶段就是将前端创建的存储卷 挂载到对应的主机上去了
            Csi.ControllerPublishVolumeResponse.Builder builder = Csi.ControllerPublishVolumeResponse.getDefaultInstance().toBuilder();
            // 获取当前环境
            StorageCsiConfig.CloudType cloudType = StorageCsiConfig.CloudType.valueOf(StorageCsiConfig.getInstance().getCloudEnv());
            switch (cloudType) {
                case ALI_YUN -> log.info("使用阿里云环境创建volume");
                case TENCENT_YUN -> log.info("使用腾讯云环境创建volume");
                case HUAWEI_YUN -> log.info("使用华为云环境创建volume");
                case LOCAL -> {
                    // 本地环境挂载
                    Csi.ControllerPublishVolumeResponse response = builder.putPublishContext("local", publishVolume.toString()).build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
                default -> {
                    log.error("不支持的云环境");
                    responseObserver.onError(Status.INTERNAL
                            .withDescription(String.format("Method %s is not support cloud env",
                                    "controllerPublishVolume"))
                            .asRuntimeException());
                }
            }
        }

        @Override
        public void controllerUnpublishVolume(Csi.ControllerUnpublishVolumeRequest request, StreamObserver<Csi.ControllerUnpublishVolumeResponse> responseObserver) {
            log.info("收到volume unPublish请求:{}", request);
            super.controllerUnpublishVolume(request, responseObserver);
        }

        @Override
        public void validateVolumeCapabilities(Csi.ValidateVolumeCapabilitiesRequest request, StreamObserver<Csi.ValidateVolumeCapabilitiesResponse> responseObserver) {
            log.info("收到volume 验证请求:{}", request);
            super.validateVolumeCapabilities(request, responseObserver);
        }

        @Override
        public void listVolumes(Csi.ListVolumesRequest request, StreamObserver<Csi.ListVolumesResponse> responseObserver) {
            log.info("收到volume list请求:{}", request);
            super.listVolumes(request, responseObserver);
        }

        @Override
        public void getCapacity(Csi.GetCapacityRequest request, StreamObserver<Csi.GetCapacityResponse> responseObserver) {
            log.info("收到capacity请求:{}", request);
            super.getCapacity(request, responseObserver);
        }

        @Override
        public void controllerGetCapabilities(Csi.ControllerGetCapabilitiesRequest request, StreamObserver<Csi.ControllerGetCapabilitiesResponse> responseObserver) {
            log.info("收到get capacity请求:{}", request);
            Csi.ControllerGetCapabilitiesResponse.Builder builder = Csi.ControllerGetCapabilitiesResponse.getDefaultInstance().toBuilder();
            Csi.ControllerGetCapabilitiesResponse response = builder
                    .addCapabilities(Csi.ControllerServiceCapability.newBuilder()
                            .setRpc(Csi.ControllerServiceCapability.RPC.newBuilder()
                                    .setType(
                                            Csi.ControllerServiceCapability.RPC.Type.EXPAND_VOLUME
                                    )
                                    .build())
                            .build())
                    .addCapabilities(Csi.ControllerServiceCapability.newBuilder()
                            .setRpc(Csi.ControllerServiceCapability.RPC.newBuilder()
                                    .setType(
                                            Csi.ControllerServiceCapability.RPC.Type.CREATE_DELETE_VOLUME
                                    )
                                    .build())
                            .build())
                    .addCapabilities(Csi.ControllerServiceCapability.newBuilder()
                            .setRpc(Csi.ControllerServiceCapability.RPC.newBuilder()
                                    .setType(
                                            Csi.ControllerServiceCapability.RPC.Type.GET_VOLUME
                                    )
                                    .build())
                            .build())
                    .addCapabilities(Csi.ControllerServiceCapability.newBuilder()
                            .setRpc(Csi.ControllerServiceCapability.RPC.newBuilder()
                                    .setType(
                                            Csi.ControllerServiceCapability.RPC.Type.LIST_VOLUMES
                                    )
                                    .build())
                            .build())
                    .addCapabilities(Csi.ControllerServiceCapability.newBuilder()
                            .setRpc(Csi.ControllerServiceCapability.RPC.newBuilder()
                                    .setType(
                                            Csi.ControllerServiceCapability.RPC.Type.PUBLISH_UNPUBLISH_VOLUME
                                    )
                                    .build())
                            .build())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void createSnapshot(Csi.CreateSnapshotRequest request, StreamObserver<Csi.CreateSnapshotResponse> responseObserver) {
            log.info("收到创建快照请求:{}", request);
            super.createSnapshot(request, responseObserver);
        }

        @Override
        public void deleteSnapshot(Csi.DeleteSnapshotRequest request, StreamObserver<Csi.DeleteSnapshotResponse> responseObserver) {
            log.info("收到删除快照请求:{}", request);
            super.deleteSnapshot(request, responseObserver);
        }

        @Override
        public void listSnapshots(Csi.ListSnapshotsRequest request, StreamObserver<Csi.ListSnapshotsResponse> responseObserver) {
            log.info("收到查看快照列表请求:{}", request);
            super.listSnapshots(request, responseObserver);
        }

        @Override
        public void controllerExpandVolume(Csi.ControllerExpandVolumeRequest request, StreamObserver<Csi.ControllerExpandVolumeResponse> responseObserver) {
            log.info("收到volume扩容请求:{}", request);
            super.controllerExpandVolume(request, responseObserver);
        }

        @Override
        public void controllerGetVolume(Csi.ControllerGetVolumeRequest request, StreamObserver<Csi.ControllerGetVolumeResponse> responseObserver) {
            log.info("收到查看volume请求:{}", request);
            super.controllerGetVolume(request, responseObserver);
        }
    }

    /**
     * 对于节点的能力
     * 主要处理mount阶段 将磁盘格式化并挂载
     */
    private static class NodeService extends NodeGrpc.NodeImplBase {
        @Override
        public void nodeStageVolume(Csi.NodeStageVolumeRequest request, StreamObserver<Csi.NodeStageVolumeResponse> responseObserver) {
            log.info("收到node stage volume请求:{}", request);
            super.nodeStageVolume(request, responseObserver);
        }

        @Override
        public void nodeUnstageVolume(Csi.NodeUnstageVolumeRequest request, StreamObserver<Csi.NodeUnstageVolumeResponse> responseObserver) {
            log.info("收到node unstage volume请求:{}", request);
            super.nodeUnstageVolume(request, responseObserver);
        }

        @Override
        public void nodePublishVolume(Csi.NodePublishVolumeRequest request, StreamObserver<Csi.NodePublishVolumeResponse> responseObserver) {
            log.info("收到node publish volume请求:{}", request);
            super.nodePublishVolume(request, responseObserver);
        }

        @Override
        public void nodeUnpublishVolume(Csi.NodeUnpublishVolumeRequest request, StreamObserver<Csi.NodeUnpublishVolumeResponse> responseObserver) {
            log.info("收到node un publish volume请求:{}", request);
            super.nodeUnpublishVolume(request, responseObserver);
        }

        @Override
        public void nodeGetVolumeStats(Csi.NodeGetVolumeStatsRequest request, StreamObserver<Csi.NodeGetVolumeStatsResponse> responseObserver) {
            log.info("收到node get volume stats请求:{}", request);
            super.nodeGetVolumeStats(request, responseObserver);
        }

        @Override
        public void nodeExpandVolume(Csi.NodeExpandVolumeRequest request, StreamObserver<Csi.NodeExpandVolumeResponse> responseObserver) {
            log.info("收到node expand volume 请求:{}", request);
            super.nodeExpandVolume(request, responseObserver);
        }

        @Override
        public void nodeGetCapabilities(Csi.NodeGetCapabilitiesRequest request, StreamObserver<Csi.NodeGetCapabilitiesResponse> responseObserver) {
            log.info("收到node get capabilities 请求:{}", request);
            super.nodeGetCapabilities(request, responseObserver);
        }

        @SuppressWarnings("AlibabaSwitchStatement")
        @Override
        public void nodeGetInfo(Csi.NodeGetInfoRequest request, StreamObserver<Csi.NodeGetInfoResponse> responseObserver) {
            log.info("收到node get info 请求:{}", request);
            Csi.NodeGetInfoResponse.Builder builder = Csi.NodeGetInfoResponse.getDefaultInstance().toBuilder();
            // 获取当前环境
            StorageCsiConfig.CloudType cloudType = StorageCsiConfig.CloudType.valueOf(StorageCsiConfig.getInstance().getCloudEnv());
            switch (cloudType) {
                case ALI_YUN -> log.info("使用阿里云环境创建volume");
                case TENCENT_YUN -> log.info("使用腾讯云环境创建volume");
                case HUAWEI_YUN -> log.info("使用华为云环境创建volume");
                case LOCAL -> {
                    Csi.NodeGetInfoResponse response = builder.setNodeId(Metadata.getInstance().getNodeId())
                            .setMaxVolumesPerNode(7)
                            .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
                default -> {
                    log.error("不支持的云环境");
                    responseObserver.onNext(null);
                    responseObserver.onCompleted();
                }
            }
        }
    }

}
