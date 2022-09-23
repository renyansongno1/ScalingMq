package org.scalingmq.storage.core.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.PartitionMsgStorage;
import org.scalingmq.storage.core.StorageClass;
import org.scalingmq.storage.core.cons.StorageAppendResult;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * 直接内存存储实现
 * @author renyansong
 */
@Slf4j
public class DirectBufferStorage implements StorageClass {

    /**
     * jvm在容器环境最大可以使用的内存比例
     */
    private static final String MAX_RAM_PERCENTAGE = "MaxRAMPercentage";

    /**
     * 写位点
     */
    private long wrote = 0L;

    /**
     * 索引写位点
     */
    private long indexWrote = 0L;

    /**
     * 最大容量
     */
    private int maxCapacity = 0;

    /**
     * 索引能够使用的最大容量
     */
    private int maxIndexCapacity = 0;

    public DirectBufferStorage() {
    }

    private void init() {
        // 查询可以使用的空间
        int maxJvmUseMemoryPercentage = 0;
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        log.debug("当前系统的jvm参数: {}", Arrays.toString(new List[]{inputArguments}));
        for (String vmArgument : inputArguments) {
            if (vmArgument.contains(MAX_RAM_PERCENTAGE)) {
                String[] percentageSplit = vmArgument.split("=");
                maxJvmUseMemoryPercentage = Integer.parseInt(percentageSplit[1]);
                break;
            }
        }
        if (maxJvmUseMemoryPercentage == 0) {
            // 不能使用内存来存储 不清楚大小
            return;
        }
        log.debug("开启直接内存存储数据...");

        // 获取当前系统的最大可使用的内存 bytes
        long maxMemoryBytes = Runtime.getRuntime().totalMemory();
        log.debug("当前系统最大可使用内存:{} bytes",  maxMemoryBytes);

        long containerRemainingMemory = maxMemoryBytes / (100 - maxJvmUseMemoryPercentage) * 100;
        // 分配可使用的堆外内存
        int maxAllCapacity = Math.toIntExact(
                containerRemainingMemory * StorageConfig.getInstance().getMsgUseMaxDirectMemoryCapacity() / 100
        );
        // 计算索引文件能够使用的内存
        maxIndexCapacity = maxAllCapacity * StorageConfig.getInstance().getIndexSpaceRatio() / 100;
        // 消息数据能够使用的就是剩下的
        maxCapacity = maxAllCapacity - maxIndexCapacity;

        log.debug("消息存储可以使用的内存:{} bytes", maxCapacity);
        log.debug("索引存储可以使用的内存:{} bytes", maxIndexCapacity);

        // 注册
        PartitionMsgStorage.getInstance().addStorageClass(storagePriority(), this);
    }

    @Override
    public int storagePriority() {
        return -1;
    }

    @Override
    public StorageAppendResult append(byte[] msgBody) {
        if (wrote+ msgBody.length > maxCapacity) {
            return StorageAppendResult.builder()
                    .success(false)
                    .build();
        }
        wrote += msgBody.length;
        ByteBuffer.allocateDirect(msgBody.length).put(msgBody);
        return StorageAppendResult.builder()
                .success(true)
                .offset(wrote)
                .build();
    }

    @Override
    public StorageAppendResult appendIndex(byte[] indexBody) {
        if (indexWrote+ indexBody.length > maxIndexCapacity) {
            return StorageAppendResult.builder()
                    .success(false)
                    .build();
        }
        indexWrote += indexBody.length;
        ByteBuffer.allocateDirect(indexBody.length).put(indexBody);
        return StorageAppendResult.builder()
                .success(true)
                .offset(indexWrote)
                .build();
    }

    @Override
    public void componentStart() {
        init();
    }

    @Override
    public void componentStop() {
        // TODO: 2022/9/18 写数据到持久存储
    }
}
