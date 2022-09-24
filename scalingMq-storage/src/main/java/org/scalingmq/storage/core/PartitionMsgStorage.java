package org.scalingmq.storage.core;

import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;
import org.scalingmq.storage.core.cons.PutIndexEntry;
import org.scalingmq.storage.core.cons.StorageAppendResult;
import org.scalingmq.common.lifecycle.Lifecycle;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;

/**
 * 分区消息存储实现
 * @author renyansong
 */
public class PartitionMsgStorage implements Lifecycle {

    private final MpscArrayQueue<PutIndexEntry> putIndexEntryMpscArrayQueue = new MpscArrayQueue<>(100000);

    private static volatile boolean STOP = false;

    /**
     * k -> 存储优先级
     * v -> 存储实现类
     */
    private static final Map<Integer, StorageClass> STORAGE_CLASS_MAP = new TreeMap<>();

    private long globalIndexWrote = 0L;

    public PartitionMsgStorage() {

    }

    /**
     * 添加存储实现
     * @param storagePriority 存储优先级
     * @param storageClass 存储实现
     */
    public void addStorageClass(int storagePriority, StorageClass storageClass) {
        synchronized (this) {
            STORAGE_CLASS_MAP.put(storagePriority, storageClass);
        }
    }

    /**
     * 追加消息到存储组件中
     * @param msgBody 消息数据
     * @return 消息物理偏移量
     */
    public long append(byte[] msgBody) {
        long appendOffset = 0L;
        int storageFlag = 0;
        synchronized (this) {
            for (Map.Entry<Integer, StorageClass> storageClassEntry : STORAGE_CLASS_MAP.entrySet()) {
                StorageAppendResult appendResult = storageClassEntry.getValue().append(msgBody);
                if (!appendResult.getSuccess()) {
                    continue;
                }
                // 成功
                appendOffset = appendResult.getOffset();
                storageFlag = storageClassEntry.getKey();
                break;
            }
        }
        if (appendOffset == 0L) {
            return 0L;
        }
        // put index
        putIndexEntryMpscArrayQueue.offer(PutIndexEntry.builder()
                .storagePriorityFlag(storageFlag)
                .msgSize(msgBody.length)
                .storageOffset(appendOffset)
                .build());

        // replicate


        return 0L;
    }

    /**
     * 获取全局写入的最大位点
     * @return 位点
     */
    public long getGlobalIndexWrote() {
        return globalIndexWrote;
    }

    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    @Override
    public void componentStart() {
        new Thread(new AppendIndexTask(), "smq-append-index-thread").start();
    }

    @Override
    public void componentStop() {
        STOP = true;
    }

    /**
     * index put的异步任务
     */
    private class AppendIndexTask implements Runnable {

        private static final int STORAGE_PRIORITY_FLAG = 4;

        private static final int STORAGE_PHYSICAL_OFFSET = 8;

        private static final int STORAGE_MSG_SIZE = 4;

        private static final int INDEX_SIZE = STORAGE_PRIORITY_FLAG + STORAGE_PHYSICAL_OFFSET + STORAGE_MSG_SIZE;

        private static final TreeMap<Long, Integer> INDEX_STORAGE_MAP = new TreeMap<>();

        @Override
        public void run() {
            while (!STOP) {
                PutIndexEntry putIndexEntry = putIndexEntryMpscArrayQueue.poll();
                if (putIndexEntry == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    continue;
                }
                putIndex(putIndexEntry);
            }

            // 将队列剩余的index 刷进存储
            while (putIndexEntryMpscArrayQueue.peek() != null) {
                PutIndexEntry putIndexEntry = putIndexEntryMpscArrayQueue.poll();
                putIndex(putIndexEntry);
            }

            // TODO: 2022/9/19 持久化索引元数据 INDEX_STORAGE_MAP
        }

        /**
         * 将索引存储起来
         * @param putIndexEntry 索引项
         */
        private void putIndex(PutIndexEntry putIndexEntry) {
            ByteBuffer indexBuf = ByteBuffer.allocate(INDEX_SIZE);
            indexBuf.putInt(putIndexEntry.getStoragePriorityFlag());
            indexBuf.putLong(putIndexEntry.getStorageOffset());
            indexBuf.putInt(putIndexEntry.getMsgSize());
            for (Map.Entry<Integer, StorageClass> storageClassEntry : STORAGE_CLASS_MAP.entrySet()) {
                StorageAppendResult appendResult = storageClassEntry.getValue().append(indexBuf.array());
                if (!appendResult.getSuccess()) {
                    continue;
                }
                globalIndexWrote += putIndexEntry.getMsgSize();
                Map.Entry<Long, Integer> offsetStorageFlagEntry = INDEX_STORAGE_MAP.lastEntry();
                if (offsetStorageFlagEntry == null) {
                    INDEX_STORAGE_MAP.put(globalIndexWrote, storageClassEntry.getKey());
                    break;
                }
                if (!offsetStorageFlagEntry.getValue().equals(storageClassEntry.getKey())) {
                    INDEX_STORAGE_MAP.put(globalIndexWrote, storageClassEntry.getKey());
                }
                break;
            }
        }

    }

}
