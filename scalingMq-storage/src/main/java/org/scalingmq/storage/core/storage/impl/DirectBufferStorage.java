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
     * 写位点
     */
    private final LongAdder wrote = new LongAdder();

    /**
     * 最大容量
     */
    private int maxCapacity = 0;

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
        maxCapacity = Math.toIntExact(
                containerRemainingMemory * StorageConfig.getInstance().getMsgUseMaxDirectMemoryCapacity() / 100
        );
        STORAGE_BUFFER = ByteBuffer.allocateDirect(
                maxCapacity
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
        synchronized (this) {
            if (wrote.intValue() + msgBody.length > maxCapacity) {
                return StorageAppendResult.builder()
                        .success(false)
                        .build();
            }
            wrote.add(msgBody.length);
            STORAGE_BUFFER.put(msgBody);
            return StorageAppendResult.builder()
                    .success(true)
                    .offset(wrote.longValue())
                    .build();
        }
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
