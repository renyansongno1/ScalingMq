package org.scalingmq.route.server.handler.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.route.client.entity.FetchTopicMetadataReqWrapper;
import org.scalingmq.route.client.entity.FetchTopicMetadataResultWrapper;
import org.scalingmq.route.client.entity.RouteResWrapper;
import org.scalingmq.route.meta.MetaDataManager;
import org.scalingmq.route.meta.schema.PartitionMetadata;
import org.scalingmq.route.meta.schema.TopicMetadata;
import org.scalingmq.route.server.handler.RequestHandler;

import java.util.List;

/**
 * 追加消息的请求处理器
 * @author renyansong
 */
@Slf4j
public class FetchTopicMetadataHandler implements RequestHandler<FetchTopicMetadataReqWrapper.FetchTopicMetadataReq> {

    private static final Gson GSON = new Gson();

    @Override
    public void handle(FetchTopicMetadataReqWrapper.FetchTopicMetadataReq fetchTopicMetadataReq, Channel channel) {
        try {
            log.debug("查询topic元数据请求:{}", fetchTopicMetadataReq.toString());
            TopicMetadata topicMetadata = MetaDataManager.getInstance().getTopicMetadata(fetchTopicMetadataReq.getTopicName());
            RouteResWrapper.RouteApiRes.Builder builder = RouteResWrapper.RouteApiRes.newBuilder();
            if (topicMetadata == null) {
                channel.writeAndFlush(builder.build());
                return;
            }
            FetchTopicMetadataResultWrapper.FetchTopicMetadataResult.Builder fetchResultBuilder = FetchTopicMetadataResultWrapper.FetchTopicMetadataResult.newBuilder()
                    .setTopicName(topicMetadata.getTopicName())
                    .setPartitionNums(topicMetadata.getPartitionNums())
                    .setReplicateFactor(topicMetadata.getReplicateFactor());
            // 构建数组
            String partitionMetadataListStr = topicMetadata.getPartitionMetadataList();
            List<PartitionMetadata> partitionMetadataList = null;
            if (partitionMetadataListStr != null) {
                partitionMetadataList = GSON.fromJson(partitionMetadataListStr, new TypeToken<List<PartitionMetadata>>(){}.getType());
            }
            if (partitionMetadataList != null && partitionMetadataList.size() > 0) {
                for (PartitionMetadata partitionMetadata : partitionMetadataList) {
                    FetchTopicMetadataResultWrapper.FetchTopicMetadataResult.PartitionMetadata.Builder partitionBuilder
                            = FetchTopicMetadataResultWrapper.FetchTopicMetadataResult.PartitionMetadata.newBuilder();
                    partitionBuilder.setPartitionNum(partitionMetadata.getPartitionNum());
                    partitionBuilder.addAllIsrStoragePodNums(partitionMetadata.getIsrStoragePodNums());
                    partitionBuilder.addAllStoragePodNums(partitionMetadata.getStoragePodNums());

                    fetchResultBuilder.addPartitionMetadataList(partitionBuilder);
                }
            }
            channel.writeAndFlush(builder.setFetchTopicMetadataResult(
                    fetchResultBuilder
                    ).build());
        } catch (Exception e) {
            log.error("响应topic metadata获取失败, 请求:{}", fetchTopicMetadataReq, e);
            RouteResWrapper.RouteApiRes res = RouteResWrapper.RouteApiRes.newBuilder()
                    .setErrorCode(RouteResWrapper.RouteApiRes.ErrorCode.UNKNOWN)
                    .setErrorMsg(e.getMessage())
                    .build();
            channel.writeAndFlush(res);
        }
    }

}
