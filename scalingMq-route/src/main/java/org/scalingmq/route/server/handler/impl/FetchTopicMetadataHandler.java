package org.scalingmq.route.server.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.route.client.entity.FetchTopicMetadataReqWrapper;
import org.scalingmq.route.client.entity.FetchTopicMetadataResultWrapper;
import org.scalingmq.route.client.entity.RouteResWrapper;
import org.scalingmq.route.meta.MetaDataManager;
import org.scalingmq.route.meta.schema.PartitionMetadata;
import org.scalingmq.route.meta.schema.TopicMetadata;
import org.scalingmq.route.server.handler.RequestHandler;

/**
 * 追加消息的请求处理器
 * @author renyansong
 */
@Slf4j
public class FetchTopicMetadataHandler implements RequestHandler<FetchTopicMetadataReqWrapper.FetchTopicMetadataReq, RouteResWrapper.RouteApiRes> {

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
                .setPartitionNums(Integer.parseInt(topicMetadata.getPartitionNums()));
        // 构建数组
        if (topicMetadata.getPartitionMetadataList() != null && topicMetadata.getPartitionMetadataList().size() > 0) {
            for (int i = 0; i < topicMetadata.getPartitionMetadataList().size(); i++) {
                PartitionMetadata partitionMetadata = topicMetadata.getPartitionMetadataList().get(i);

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
                        partitionBuilder.build()
                        );
            }
        }
        return builder.setFetchTopicMetadataResult(
                fetchResultBuilder.build()
                ).build();
    }

}
