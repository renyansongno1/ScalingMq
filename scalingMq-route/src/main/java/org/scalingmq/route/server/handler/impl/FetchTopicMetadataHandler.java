package org.scalingmq.route.server.handler.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
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
public class FetchTopicMetadataHandler implements RequestHandler<FetchTopicMetadataReqWrapper.FetchTopicMetadataReq, RouteResWrapper.RouteApiRes> {

    private static final Gson GSON = new Gson();

    @Override
    public RouteResWrapper.RouteApiRes handle(FetchTopicMetadataReqWrapper.FetchTopicMetadataReq fetchTopicMetadataReq) {
        log.debug("查询topic元数据请求:{}", fetchTopicMetadataReq.toString());
        TopicMetadata topicMetadata = MetaDataManager.getInstance().getTopicMetadata(fetchTopicMetadataReq.getTopicName());
        RouteResWrapper.RouteApiRes.Builder builder = RouteResWrapper.RouteApiRes.newBuilder();
        if (topicMetadata == null) {
            return builder.build();
        }
        FetchTopicMetadataResultWrapper.FetchTopicMetadataResult.Builder fetchResultBuilder = FetchTopicMetadataResultWrapper.FetchTopicMetadataResult.newBuilder()
                .setTopicName(topicMetadata.getTopicName())
                .setPartitionNums(topicMetadata.getPartitionNums());
        // 构建数组
        String partitionMetadataListStr = topicMetadata.getPartitionMetadataList();
        List<PartitionMetadata> partitionMetadataList = null;
        if (partitionMetadataListStr != null) {
            partitionMetadataList = GSON.fromJson(partitionMetadataListStr, new TypeToken<List<PartitionMetadata>>(){}.getType());
        }
        if (partitionMetadataList != null && partitionMetadataList.size() > 0) {
            for (int i = 0; i < partitionMetadataList.size(); i++) {
                PartitionMetadata partitionMetadata = partitionMetadataList.get(i);

                FetchTopicMetadataResultWrapper.FetchTopicMetadataResult.PartitionMetadata.Builder partitionBuilder
                        = FetchTopicMetadataResultWrapper.FetchTopicMetadataResult.PartitionMetadata.newBuilder();
                if (partitionMetadata.getIsrStoragePodNums() != null && partitionMetadata.getIsrStoragePodNums().size() > 0) {
                    for (int j = 0; j < partitionMetadata.getIsrStoragePodNums().size(); j++) {
                        partitionBuilder.setIsrStoragePodNums(j, partitionMetadata.getIsrStoragePodNums().get(j));
                    }
                }

                if (partitionMetadata.getStoragePodNums() != null && partitionMetadata.getStoragePodNums().size() > 1) {
                    for (int k = 0; k < partitionMetadata.getStoragePodNums().size(); k++) {
                        partitionBuilder.setStoragePodNums(k, partitionMetadata.getStoragePodNums().get(k));
                    }
                }

                fetchResultBuilder.setPartitionMetadataList(i,
                        partitionBuilder
                        );
            }
        }
        return builder.setFetchTopicMetadataResult(
                fetchResultBuilder
                ).build();
    }

}
