package org.scalingmq.storage.core.storage;

import org.scalingmq.storage.core.cons.StorageAppendResult;
import org.scalingmq.common.lifecycle.Lifecycle;
import org.scalingmq.storage.core.storage.entity.StorageFetchMsgResult;

/**
 * 存储介质接口
 * @author renyansong
 */
public interface StorageClass extends Lifecycle{

    /**
     * 当前存储介质实现的优先级
     * @return 优先级数据 从小到大，越小越优先
     */
    int storagePriority();

    /**
     * 追加数据
     * @param msgBody 消息数据
     * @return 追加数据结果
     */
    StorageAppendResult append(byte[] msgBody);

    /**
     * 追加索引数据
     * @param indexBody 索引内容
     * @param globalIndexPosition 全局索引写位点
     * @return 追加结果
     */
    StorageAppendResult appendIndex(byte[] indexBody, long globalIndexPosition);

    /**
     * 拉取Index数据
     * @param storagePosition 物理偏移量
     * @param indexSize 拉取的数据的大小
     * @return 数据
     */
    byte[] fetchDataFromIndex(long storagePosition, int indexSize);

    /**
     * 拉取消息数据
     * @param physicalOffset 物理偏移量
     * @param msgSize 消息大小
     * @param maxFetchMsgMb 最大拉取消息的大小
     * @return 拉取结果
     */
    StorageFetchMsgResult fetchFromMsg(long physicalOffset, int msgSize, String maxFetchMsgMb);
}
