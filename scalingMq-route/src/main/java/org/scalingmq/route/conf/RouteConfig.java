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
     * 动态生成的storage pod的msg端口
     */
    @EnvironmentVariable("SCHEDULE_STORAGE_POD_PORT")
    private String scheduleStoragePodPort;

    /**
     * 动态生成的storage pod的msg端口名称
     */
    @EnvironmentVariable("SCHEDULE_STORAGE_POD_PORT_NAME")
    private String scheduleStoragePodPortName;

    /**
     * 动态生成的storage pod的raft端口
     */
    @EnvironmentVariable("SCHEDULE_STORAGE_POD_RAFT_PORT")
    private String scheduleStoragePodRaftPort;

    /**
     * 动态生成的storage pod的raft端口名称
     */
    @EnvironmentVariable("SCHEDULE_STORAGE_POD_RAFT_PORT_NAME")
    private String scheduleStoragePodRaftPortName;

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

    /**
     * 创建出来的storage pod需要的coordinator的比例
     * 不会影响原来本身的数量
     */
    @EnvironmentVariable("SCHEDULE_STORAGE_COORDINATOR_RATIO")
    private String scheduleStorageCoordinatorRatio;

    /**
     * 业务处理线程数量
     */
    private Integer serviceThreadCount = 10;

}
