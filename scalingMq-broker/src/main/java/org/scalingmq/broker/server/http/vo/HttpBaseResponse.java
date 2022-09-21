package org.scalingmq.broker.server.http.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * http相关的基础响应
 * @author renyansong
 */
@Getter
@Setter
@ToString
@SuperBuilder
public class HttpBaseResponse {

    /**
     * 操作结果
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errMsg;

}
