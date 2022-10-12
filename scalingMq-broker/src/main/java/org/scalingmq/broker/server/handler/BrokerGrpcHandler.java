package org.scalingmq.broker.server.handler;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.conf.BrokerConfig;
import org.scalingmq.broker.exception.MetadataFetchException;
import org.scalingmq.broker.exception.ProduceException;
import org.scalingmq.client.grpc.ProduceReqWrapper;
import org.scalingmq.client.grpc.ProduceResWrapper;
import org.scalingmq.client.grpc.ScalingmqServiceGrpc;
import org.scalingmq.common.net.NetworkClient;
import org.scalingmq.route.client.RouteAppClient;
import org.scalingmq.route.client.conf.RouteClientConfig;
import org.scalingmq.route.client.entity.FetchTopicMetadataReqWrapper;
import org.scalingmq.route.client.entity.FetchTopicMetadataResultWrapper;
import org.scalingmq.route.client.entity.RouteResWrapper;
import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.storage.api.StorageApiResWrapper;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * grpc的代理实现
 * @author renyansong
 */
@Slf4j
public class BrokerGrpcHandler extends ScalingmqServiceGrpc.ScalingmqServiceImplBase {

    private static final RouteAppClient ROUTE_APP_CLIENT = RouteAppClient.getInstance();

    private static final LongAdder ROLL_PARTITION_COUNTER = new LongAdder();

    private final ThreadPoolExecutor netThreadPool;

    public BrokerGrpcHandler() {
        init();
        // 初始化线程池
        netThreadPool = new ThreadPoolExecutor(BrokerConfig.getInstance().getStorageClientThreadCount(),
                BrokerConfig.getInstance().getStorageClientThreadCount(),
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadFactory() {
                    final AtomicInteger index = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "storage-client-net-thread-" + index.getAndIncrement());
                    }
                });
    }

    private void init() {
        RouteAppClient.getInstance().initConfig(
                RouteClientConfig.builder()
                        .serverAddr(BrokerConfig.getInstance().getRouteServerAddr())
                        .serverPort(Integer.valueOf(BrokerConfig.getInstance().getRouteServerPort()))
                        .threadCount(BrokerConfig.getInstance().getRouteClientThreadCount())
                        .build()
        );
    }

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

        // 获取元数据
        FetchTopicMetadataResultWrapper.FetchTopicMetadataResult fetchTopicMetadataResult;
        try {
            fetchTopicMetadataResult
                    = ROUTE_APP_CLIENT.fetchTopicMetadata(FetchTopicMetadataReqWrapper.FetchTopicMetadataReq.newBuilder()
                    .setTopicName(request.getTopic())
                    .build());
        } catch (Exception e) {
            log.error("获取元数据异常", e);
            ProduceResWrapper.ProduceMsgRes res = ProduceResWrapper.ProduceMsgRes.newBuilder()
                    .setErrorCode(MetadataFetchException.MSG)
                    .setErrorMsg(e.getMessage())
                    .build();
            responseObserver.onNext(res);
            responseObserver.onCompleted();
            return;
        }
        List<FetchTopicMetadataResultWrapper.FetchTopicMetadataResult.PartitionMetadata> partitionMetadataList
                = fetchTopicMetadataResult.getPartitionMetadataListList();

        String maybeLeaderAddr;
        if (!"".equals(request.getMsgKey())) {
            // 按照key来取模分配分区
            String msgKey = request.getMsgKey();
            int index = msgKey.hashCode() % partitionMetadataList.size();
            FetchTopicMetadataResultWrapper.FetchTopicMetadataResult.PartitionMetadata choosePartition
                    = partitionMetadataList.get(index);
            // 默认取第一个是leader
            maybeLeaderAddr = choosePartition.getIsrStoragePodNumsList().get(0);
        } else {
            // 正常轮询选择一个分区
            int index = Math.toIntExact(ROLL_PARTITION_COUNTER.longValue() % partitionMetadataList.size());
            FetchTopicMetadataResultWrapper.FetchTopicMetadataResult.PartitionMetadata choosePartition
                    = partitionMetadataList.get(index);
            // 默认取第一个是leader
            maybeLeaderAddr = choosePartition.getIsrStoragePodNumsList().get(0);
            ROLL_PARTITION_COUNTER.add(1);
        }
        // 构造发送消息的请求
        StorageApiReqWrapper.StorageApiReq storageApiReq = StorageApiReqWrapper.StorageApiReq.newBuilder()
                .setApiType(StorageApiReqWrapper.StorageApiReq.ApiType.PRODUCT)
                .setPutMsgReq(StorageApiReqWrapper.StorageApiReq.PutMsgReq.newBuilder()
                        .setMsgItems(0, StorageApiReqWrapper.StorageApiReq.PutMsgReq.MsgItem.newBuilder()
                                .setContent(request.getMessage()).build())
                        .build())
                .build();

        try {
            StorageApiResWrapper.StorageApiRes storageApiRes = netThreadPool.submit(new NetCallTask(storageApiReq, maybeLeaderAddr, null)).get();
            // 解析发送消息的响应
            if (!"".equals(storageApiRes.getErrorCode())) {
                log.error("消息发送失败, req:{}, res:{}", storageApiReq, storageApiRes);
                ProduceResWrapper.ProduceMsgRes res = ProduceResWrapper.ProduceMsgRes.newBuilder()
                        .setErrorCode(storageApiRes.getErrorCode())
                        .setErrorMsg(storageApiRes.getErrorMsg())
                        .build();
                responseObserver.onNext(res);
                responseObserver.onCompleted();
                return;
            }
            StorageApiResWrapper.PutMsgRes putMsgRes = storageApiRes.getPutMsgRes();
            ProduceResWrapper.ProduceMsgRes res = ProduceResWrapper.ProduceMsgRes.newBuilder()
                    .setOffset(putMsgRes.getOffset())
                    .setMsgId(putMsgRes.getMsgId())
                    .build();
            responseObserver.onNext(res);
            responseObserver.onCompleted();
        } catch (InterruptedException | ExecutionException e) {
            log.error("请求storage error", e);
            ProduceResWrapper.ProduceMsgRes res = ProduceResWrapper.ProduceMsgRes.newBuilder()
                    .setErrorCode(ProduceException.MSG)
                    .setErrorMsg(e.getMessage())
                    .build();
            responseObserver.onNext(res);
            responseObserver.onCompleted();
        }
    }

    /**
     * 异步调用任务
     */
    private record NetCallTask(Object req, String addr, Integer port)
            implements Callable<StorageApiResWrapper.StorageApiRes> {

        @Override
        public StorageApiResWrapper.StorageApiRes call() {
            if (log.isDebugEnabled()) {
                log.debug("开始访问storage network..");
            }
            return (StorageApiResWrapper.StorageApiRes) NetworkClient.getInstance().sendReq(req,
                    addr,
                    port,
                    StorageApiResWrapper.StorageApiRes.getDefaultInstance());
        }

    }

}
