package org.scalingmq.broker.conf;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.scalingmq.common.config.EnvironmentVariable;

/**
 * Broker的config
 * @author renyansong
 */
@Getter
@Setter
@ToString
public class BrokerConfig {

    private static final BrokerConfig INSTANCE = new BrokerConfig();

    /**
     * http的端口，针对admin等管理端的开放端口
     */
    public static final int HTTP_PORT = 7654;

    /**
     * 针对消息交互的tcp端口
     */
    public static final int MSG_PORT = 6543;

    private BrokerConfig() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static BrokerConfig getInstance() {
        return INSTANCE;
    }

    /**
     * route server的地址
     */
    @EnvironmentVariable("ROUTE_SERVER_ADDR")
    private String routeServerAddr;

    /**
     * route server的端口
     */
    @EnvironmentVariable("ROUTE_SERVER_PORT")
    private String routeServerPort;

    /**
     * route client的线程数量
     */
    private Integer routeClientThreadCount = 10;

    /**
     * storage client的线程数量
     */
    private Integer storageClientThreadCount = 10;

}
