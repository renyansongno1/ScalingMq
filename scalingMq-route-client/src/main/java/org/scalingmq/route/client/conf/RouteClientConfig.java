package org.scalingmq.route.client.conf;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * client端的配置
 * @author renyansong
 */
@Getter
@Setter
@ToString
public class RouteClientConfig {

    /**
     * server端的地址
     */
    private String serverAddr;

    /**
     * server端的端口
     */
    private Integer serverPort;

    /**
     * 并发线程数
     */
    private Integer threadCount;

}
