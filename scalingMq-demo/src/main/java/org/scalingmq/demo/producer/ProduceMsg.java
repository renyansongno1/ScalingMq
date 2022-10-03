package org.scalingmq.demo.producer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 发送的消息
 * @author renyansong
 */
@Getter
@Setter
@ToString
public class ProduceMsg {

    private String topic;

    private String msg;

}
