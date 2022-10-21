package org.scalingmq.storage.core.cons;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 追加数据到存储的结果
 * @author renyansong
 */
@Getter
@Setter
@Builder
@ToString
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
