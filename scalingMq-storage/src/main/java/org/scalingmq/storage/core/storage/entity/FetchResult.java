package org.scalingmq.storage.core.storage.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 拉取消息数据结果
 * @author renyansong
 */
@Getter
@Setter
@Builder
public class FetchResult {

    /**
     * 是否没有数据
     */
    private Boolean noResult;

    /**
     * 拉取到的数据
     */
    private List<byte[]> fetchDataList;

    /**
     * 拉取最后的offset
     */
    private Long fetchLastOffset;
}
