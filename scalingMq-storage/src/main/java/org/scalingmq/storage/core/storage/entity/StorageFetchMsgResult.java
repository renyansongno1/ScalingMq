package org.scalingmq.storage.core.storage.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 存储组件拉取数据的大小
 * @author renyansong
 */
@Getter
@Setter
@Builder
public class StorageFetchMsgResult {

    /**
     * 拉取的消息数据
     */
    private List<byte[]> msgDataList;

    /**
     * 拉取的消息条数
     */
    private Integer fetchMsgItemCount;

}
