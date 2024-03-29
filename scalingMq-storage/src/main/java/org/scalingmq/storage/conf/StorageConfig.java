package org.scalingmq.storage.conf;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.scalingmq.common.config.EnvironmentVariable;

import java.util.List;

/**
 * 相关的配置
 * @author renyansong
 */
@Getter
@Setter
@ToString
public class StorageConfig {

    private static final StorageConfig INSTANCE = new StorageConfig();

    public static final int MSG_PORT = 9876;

    public static final int RAFT_PORT = 4321;

    private StorageConfig() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static StorageConfig getInstance() {
        return INSTANCE;
    }

    /**
     * 最大使用的堆外内存比例
     */
    private int msgUseMaxDirectMemoryCapacity = 70;

    /**
     * 持久内存挂载的路径
     */
    private String pmemMountPath;

    /**
     * 磁盘挂载路径
     */
    private String diskMountPath;

    /**
     * ssd挂载路径
     */
    private String ssdMountPath;

    /**
     * 刷盘系数 4K的多少倍
     */
    private Integer flushDiskFactor = 1;

    /**
     * 分区消息文件名称
     */
    private String partitionFileName;

    /**
     * 分区索引文件名称
     */
    private String partitionIndexFileName;

    /**
     * 索引默认占比每个存储介质 默认20% 不影响ssd和hdd 默认disk类存储无限
     */
    private Integer indexSpaceRatio = 20;

    /**
     * 当前pod的hostname
     */
    @EnvironmentVariable("HOSTNAME")
    private String hostname;

    /**
     * 当前pod所属的service name
     */
    @EnvironmentVariable("SERVICE_NAME")
    private String serviceName;

    /**
     * 当前pod所属的namespace
     */
    @EnvironmentVariable("POD_NAMESPACE")
    private String namespace = "default";

    /**
     * 协调者节点编号
     */
    @EnvironmentVariable("COORDINATOR_NUMS")
    private String coordinatorNums;

    /**
     * 每次拉取消息的最大消息流量
     */
    private String maxFetchMsgMb = "1";

    /**
     * 分区副本最大允许落后的时间
     */
    private Integer maxPartitionBackwardTime = 10 * 1000;

    /**
     * 业务线程数
     */
    private Integer serviceThreadCount = 10;

    /**
     * 元数据节点的地址
     */
    @EnvironmentVariable("ROUTE_SERVER_ADDR")
    private String routeServerAddr;

    /**
     * 元数据节点的端口
     */
    @EnvironmentVariable("ROUTE_SERVER_PORT")
    private String routeServerPort;

    /**
     * 元数据通信客户端线程数
     */
    private Integer routeClientThreadCount = 2;

    /**
     * topic
     */
    @EnvironmentVariable("TOPIC_NAME")
    private String topicName;

    /**
     * 分区号
     */
    @EnvironmentVariable("PARTITION_NUM")
    private String partitionNum;

}
