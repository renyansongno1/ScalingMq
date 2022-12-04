package org.scalingmq.storage.core.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.ioc.IocContainer;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.storage.PartitionMsgStorage;
import org.scalingmq.storage.core.storage.StorageClass;
import org.scalingmq.storage.core.cons.StorageAppendResult;
import org.scalingmq.storage.core.storage.StorageMapping;
import org.scalingmq.storage.core.storage.entity.StorageFetchMsgResult;

import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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
     * 消息数据内存池
     */
    private final List<ByteBuffer> MSG_DATA_MEMORY_BUFFER_POOL = new CopyOnWriteArrayList<>();

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
     * 等待降级存储的数据
     */
    private volatile Integer waitDemotionBufferEndIndex;

    /**
     * 降级刷数据开关
     */
    private volatile boolean demotion = false;

    /**
     * 组件状态
     */
    private static volatile boolean STOP = false;


    public DirectBufferStorage() {
    }

    private void init() {
        // 查询可以使用的空间
        int maxJvmUseMemoryPercentage = 0;
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        log.debug("当前系统的jvm参数: {}", Arrays.toString(inputArguments.toArray()));
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
        // 将可以使用的内存分为两部分，写满一半之后数据降级，使用另一部分来承接读写
        maxCapacity = maxAllCapacity / 2;

        log.debug("消息存储可以使用的内存(half):{} bytes", maxCapacity);

        // 注册
        StorageMapping.addStorageClass(storagePriority(), this);
    }

    @Override
    public int storagePriority() {
        return -1;
    }

    @Override
    public StorageAppendResult append(byte[] msgBody) {
        return appendBody(msgBody, false);
    }

    @Override
    public StorageAppendResult appendIndex(byte[] indexBody, long globalIndexPosition) {
        // index不会真实的存储在buffer pool中
        // 因为index本身就是天然有序了，数据存储是一个个 entry组成的list，那么index就是需要找到在list中的位置
        // 所以最终appendIndex的结果必然是 INDEX_SIZE * 已追加数量
        // 那么再查询消息的时候 就拿这个index的Position来查询
        return appendBody(indexBody, true);
    }

    @Override
    public byte[] fetchDataFromIndex(long storagePosition, int indexSize) {
        return null;
    }

    @Override
    public StorageFetchMsgResult fetchFromMsg(long physicalOffset, int msgSize, String maxFetchMsgMb) {
        long maxFetchMsgBytes = Long.parseLong(maxFetchMsgMb) * 1024 * 1024;
        long appendMsgBytes = 0L;
        // 直接内存模式下
        // index来查询的时候，只要 index的Position 除以 index_size 就是数据的在List的index
        long index = physicalOffset/msgSize;
        int msgCount = 0;
        List<byte[]> resultList = new ArrayList<>();
        while (appendMsgBytes < maxFetchMsgBytes && index < MSG_DATA_MEMORY_BUFFER_POOL.size()) {
            ByteBuffer buffer = MSG_DATA_MEMORY_BUFFER_POOL.get(Math.toIntExact(index));
            int limit = buffer.limit();
            byte[] data =  new byte[limit];
            buffer.get(data);
            resultList.add(data);
            appendMsgBytes += limit;
            index++;
            msgCount++;
        }
        return StorageFetchMsgResult.builder()
                .fetchMsgItemCount(msgCount)
                .msgDataList(resultList)
                .build();
    }

    private StorageAppendResult appendBody(byte[] body, boolean index) {
        long position;
        int maxMemCapacity;
        if (index) {
            position = indexWrote;
            // 索引写入的时候 由于不会真实的存储到buffer pool中，所以只要数据存储下了 就不需要考虑index的存储
            maxMemCapacity = Integer.MAX_VALUE;
        } else {
            position = wrote;
            maxMemCapacity = maxCapacity;
        }
        if (position + body.length > maxMemCapacity) {
            if (waitDemotionBufferEndIndex != null) {
                log.warn("all of direct double buffer full 检查是否需要调整参数MsgUseMaxDirectMemoryCapacity...");
                // 等待降级动作完成
                while (waitDemotionBufferEndIndex != null) {
                    try {
                        Thread.sleep(Duration.ofMillis(10));
                    } catch (InterruptedException e) {
                        // ignore...
                    }
                }
            }
            waitDemotionBufferEndIndex = MSG_DATA_MEMORY_BUFFER_POOL.size() - 1;
            // 开始降级刷数据
            wrote = 0L;
            indexWrote = 0L;
            demotion = true;
        }
        long beforeAppendPosition = position;
        if (index) {
            indexWrote += body.length;
        } else {
            wrote += body.length;
        }
        if (!index) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(body.length).put(body);
            // 回归原位 方便读取
            byteBuffer.position(0);
            MSG_DATA_MEMORY_BUFFER_POOL.add(byteBuffer);
        }
        return StorageAppendResult.builder()
                .success(true)
                .offset(beforeAppendPosition)
                .build();
    }

    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    @Override
    public void componentStart() {
        init();
        new Thread(new DemotionRefreshTask(), "demotion-storage-direct-buffer-thread").start();
    }

    @Override
    public void componentStop() {
        // TODO: 2022/9/18 写数据到持久存储
        STOP = true;
    }

    private class DemotionRefreshTask implements Runnable {

        @Override
        public void run() {
            while (!STOP) {
                if (demotion &&
                        waitDemotionBufferEndIndex != null) {
                    // 获取下一级存储 整体刷到下一级存储
                    Map.Entry<Integer, StorageClass> priorityStorageClassEntry = StorageMapping.getMapping().higherEntry(storagePriority());
                    StorageClass nextStorage = priorityStorageClassEntry.getValue();
                    PartitionMsgStorage partitionMsgStorage = IocContainer.getInstance().getObj(PartitionMsgStorage.class);
                    for (int i = 0; i <= waitDemotionBufferEndIndex; i++) {
                        ByteBuffer data = MSG_DATA_MEMORY_BUFFER_POOL.get(i);
                        partitionMsgStorage.append(data.array(), nextStorage.storagePriority());
                    }
                    // 降级写完 需要同时更新当前存储indexStorageMap信息
                    partitionMsgStorage.getIndexManager().updateIndexMetadata(waitDemotionBufferEndIndex + 1, storagePriority());
                    MSG_DATA_MEMORY_BUFFER_POOL.removeAll(MSG_DATA_MEMORY_BUFFER_POOL.subList(0, waitDemotionBufferEndIndex));
                }
            }
        }
    }

}
