package org.scalingmq.broker.server.http.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 创建topic的响应
 * @author renyansong
 */
@Getter
@Setter
@SuperBuilder
public class CreateTopicResponse extends HttpBaseResponse {

    /**
     * 创建成功的topic名称
     */
    private String topicName;

}
