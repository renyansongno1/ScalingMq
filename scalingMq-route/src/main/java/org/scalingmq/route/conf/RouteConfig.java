package org.scalingmq.route.conf;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.scalingmq.common.config.EnvironmentVariable;

/**
 * route相关配置
 * @author renyansong
 */
@Getter
@Setter
@ToString
public class RouteConfig {

    private static final RouteConfig INSTANCE = new RouteConfig();

    private RouteConfig() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static RouteConfig getInstance() {
        return INSTANCE;
    }

    /**
     * 当前pod所属的namespace
     */
    @EnvironmentVariable("POD_NAMESPACE")
    private String namespace;

    /**
     * 通信端口
     */
    @EnvironmentVariable("SERVER_PORT")
    private Integer serverPort;

}
