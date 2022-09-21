package org.scalingmq.storage.core;

import org.scalingmq.storage.core.cons.StorageAppendResult;
import org.scalingmq.common.lifecycle.Lifecycle;

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
     * @return 追加结果
     */
    StorageAppendResult appendIndex(byte[] indexBody);

}
