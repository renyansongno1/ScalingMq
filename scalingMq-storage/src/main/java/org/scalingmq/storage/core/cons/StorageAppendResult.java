package org.scalingmq.storage.core.cons;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 追加数据到存储的结果
 * @author renyansong
 */
@Getter
@Setter
@Builder
public class StorageAppendResult {

    /**
     * 追加结果
     */
    private Boolean success;

    /**
     * 物理偏移量
     */
    private Long offset;

}
