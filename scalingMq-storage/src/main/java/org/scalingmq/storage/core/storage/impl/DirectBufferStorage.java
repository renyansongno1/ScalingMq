package org.scalingmq.storage.core.storage.impl;

import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.PartitionMsgStorage;
import org.scalingmq.storage.core.StorageClass;
import org.scalingmq.storage.core.cons.StorageAppendResult;

import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.LongAdder;

/**
 * 直接内存存储实现
 * @author renyansong
 */
public class DirectBufferStorage implements StorageClass {

    /**
     * jvm在容器环境最大可以使用的内存比例
     */
    private static final String MAX_RAM_PERCENTAGE = "MaxRAMPercentage";

    /**
     * 堆外内存buffer
     */
    private static ByteBuffer STORAGE_BUFFER;

    /**
     * 索引使用的buffer
     */
    private static ByteBuffer INDEX_STORAGE_BUFFER;

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
        for (String vmArgument : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
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

        // 获取当前系统的最大可使用的内存 bytes
        long maxMemoryBytes = Runtime.getRuntime().totalMemory();
        long containerRemainingMemory = maxMemoryBytes / (100 - maxJvmUseMemoryPercentage) * 100;
        // 分配可使用的堆外内存
        int maxAllCapacity = Math.toIntExact(
                containerRemainingMemory * StorageConfig.getInstance().getMsgUseMaxDirectMemoryCapacity() / 100
        );
        // 计算索引文件能够使用的内存
        maxIndexCapacity = maxAllCapacity * StorageConfig.getInstance().getIndexSpaceRatio() / 100;
        // 消息数据能够使用的就是剩下的
        maxCapacity = maxAllCapacity - maxIndexCapacity;

        STORAGE_BUFFER = ByteBuffer.allocateDirect(
                maxCapacity
        );

        INDEX_STORAGE_BUFFER = ByteBuffer.allocateDirect(
                maxIndexCapacity
        );

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
        STORAGE_BUFFER.put(msgBody);
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
        INDEX_STORAGE_BUFFER.put(indexBody);
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
