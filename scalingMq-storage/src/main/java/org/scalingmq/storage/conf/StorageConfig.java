package org.scalingmq.storage.conf;

import lombok.Getter;
import lombok.Setter;

/**
 * 相关的配置
 * @author renyansong
 */
@Getter
@Setter
public class StorageConfig {

    private static final StorageConfig INSTANCE = new StorageConfig();

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

}
