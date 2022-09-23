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
    private String serverPort;

    /**
     * 动态生成的storage pod的端口
     */
    @EnvironmentVariable("SCHEDULE_STORAGE_POD_PORT")
    private String scheduleStoragePodPort;

    /**
     * 动态生成的storage pod image
     */
    @EnvironmentVariable("SCHEDULE_STORAGE_POD_IMAGE")
    private String scheduleStoragePodImage;

    /**
     * 动态生成的storage pod cpu限制
     */
    @EnvironmentVariable("SCHEDULE_STORAGE_POD_CPU")
    private String scheduleStoragePodCpu;

    /**
     * 动态生成的storage pod 内存限制
     */
    @EnvironmentVariable("SCHEDULE_STORAGE_POD_MEM")
    private String scheduleStoragePodMem;

}
