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

}
