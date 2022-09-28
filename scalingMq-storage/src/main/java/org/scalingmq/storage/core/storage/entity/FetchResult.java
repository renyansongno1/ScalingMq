package org.scalingmq.storage.core.storage.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    private byte[] fetchData;

    /**
     * 拉取最后的offset
     */
    private Long fetchLastOffset;
}
