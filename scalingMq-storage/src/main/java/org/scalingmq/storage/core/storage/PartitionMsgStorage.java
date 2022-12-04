package org.scalingmq.storage.core.storage;

import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.cons.PutIndexEntry;
import org.scalingmq.storage.core.cons.StorageAppendResult;
import org.scalingmq.common.lifecycle.Lifecycle;
import org.scalingmq.storage.core.storage.entity.FetchResult;
import org.scalingmq.storage.core.storage.entity.StorageFetchMsgResult;
import org.scalingmq.storage.exception.ExceptionCodeEnum;
import org.scalingmq.storage.exception.StorageBaseException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 分区消息存储实现
 * @author renyansong
 */
@Slf4j
public class PartitionMsgStorage implements Lifecycle {

    /**
     * 消息编码-消息长度描述4字节
     */
    private static final int MSG_LENGTH_SIZE = 4;

    /**
     * 消息编码-魔数
     */
    private static final String MAGIC = "SCMQ";

    /**
     * 消息魔数长度描述
     */
    private static final int MAGIC_DATA_SIZE = MAGIC.getBytes(StandardCharsets.UTF_8).length;

    private final MpscArrayQueue<PutIndexEntry> putIndexEntryMpscArrayQueue = new MpscArrayQueue<>(100000);

    private final MsgIndexManager indexManager = new MsgIndexManager();

    private static volatile boolean STOP = false;

    private long globalIndexWrote = 0L;

    public PartitionMsgStorage() {

    }

    /**
     * 追加消息到存储组件中
     * @param msgBody 消息数据
     * @param storageComponentFlag 存储组件标识
     * @return 消息物理偏移量
     */
    public long append(byte[] msgBody, Integer storageComponentFlag) {
        // 拼接消息形成最终的落盘消息
        ByteBuffer buffer = ByteBuffer.allocate(MSG_LENGTH_SIZE + MAGIC_DATA_SIZE + msgBody.length);
        buffer.putInt(msgBody.length);
        buffer.put(MAGIC.getBytes(StandardCharsets.UTF_8));
        buffer.put(msgBody);

        long appendOffset;
        int storageFlag;
        synchronized (this) {
            StorageClass storageClass;
            if (storageComponentFlag != null) {
                storageClass = StorageMapping.getMapping().get(storageComponentFlag);
                storageFlag = storageComponentFlag;
            } else {
                Map.Entry<Integer, StorageClass> storageClassEntry = StorageMapping.getMapping().firstEntry();
                storageClass = storageClassEntry.getValue();
                storageFlag = storageClassEntry.getKey();
            }
            StorageAppendResult appendResult = storageClass.append(buffer.array());
            if (!appendResult.getSuccess()) {
                // 存储组件保证写入数据必成
                log.error("write data fail. storage class:{}", storageClass);
                return 0L;
            }
            // 成功
            appendOffset = appendResult.getOffset();
        }
        if (appendOffset == 0L) {
            return 0L;
        }
        // put index
        putIndexEntryMpscArrayQueue.offer(PutIndexEntry.builder()
                .storagePriorityFlag(storageFlag)
                .msgSize(buffer.array().length)
                .storageOffset(appendOffset)
                .build());

        return appendOffset;
    }

    /**
     * 拉取消息
     * @param fetchOffset 拉取偏移量
     * @return 拉取结果
     */
    public FetchResult fetchMsg(long fetchOffset) {
        return indexManager.fetchMsgByIndex(fetchOffset);
    }

    /**
     * 获取全局写入的最大位点
     * @return 位点
     */
    public long getGlobalIndexWrote() {
        return globalIndexWrote;
    }

    public MsgIndexManager getIndexManager() {
        return indexManager;
    }

    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    @Override
    public void componentStart() {
        new Thread(indexManager, "smq-index-manager-thread").start();
    }

    @Override
    public void componentStop() {
        STOP = true;
    }

    /**
     * index put的异步任务
     */
    public class MsgIndexManager implements Runnable {

        private static final int STORAGE_PRIORITY_FLAG = 4;

        private static final int STORAGE_PHYSICAL_OFFSET = 8;

        private static final int STORAGE_MSG_SIZE = 4;

        private static final int INDEX_SIZE = STORAGE_PRIORITY_FLAG + STORAGE_PHYSICAL_OFFSET + STORAGE_MSG_SIZE;

        private static final TreeMap<Long, Integer> INDEX_STORAGE_MAP = new TreeMap<>();

        private static final ReadWriteLock INDEX_METADATA_LOCK = new ReentrantReadWriteLock();

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
         * 通过索引拉取消息
         * 没有新的消息也要返回最新的offset
         * @param fetchIndex 需要拉取的offset
         * @return 拉取结果
         */
        public FetchResult fetchMsgByIndex(long fetchIndex) {
            long fetchOffset = -1L;
            boolean noDataNew;
            if (fetchIndex >= globalIndexWrote) {
                fetchOffset = globalIndexWrote;
                noDataNew = true;
            } else {
                noDataNew = false;
            }

            if (noDataNew) {
                return FetchResult.builder()
                        .fetchLastOffset(fetchOffset)
                        .noResult(true)
                        .fetchDataList(Collections.emptyList())
                        .build();
            }

            // 查询数据
            Lock lock = INDEX_METADATA_LOCK.readLock();
            lock.lock();
            try {
                // 索引和存储组件的mapping
                // 一开始是 1-100 在 Direct组件 1:directFlag
                // 当出现降级的时候，1-100 就会写到 Disk组件, 这个就是 1:diskFlag
                // 再写消息的时候 就会是 101:directFlag
                //                     1:diskFlag
                // 所以通过要查询的Index 取低于这个Index的存储组件Flag
                Map.Entry<Long, Integer> indexOffsetAndStorageFlagEntry = INDEX_STORAGE_MAP.lowerEntry(fetchIndex);
                if (indexOffsetAndStorageFlagEntry == null ) {
                    throw new StorageBaseException(ExceptionCodeEnum.FETCH_MISS, "通过index offset 没有找到对应数据");
                }
                // 整个index是以 INDEX_SIZE 来增加的，而索引元数据的key是最早一条入当前存储类型的索引Position
                // 那偏移量就是固定的了，比如 0-100写在第一个存储 100-200写到第二个存储，当需要找110的时候(假设index size = 10)
                // 就会找到第二个存储，然后用110 - 100 = 10，就是第二个存储的当前索引开始的位点
                long storagePosition = fetchIndex - indexOffsetAndStorageFlagEntry.getKey();
                StorageClass storageClass = StorageMapping.getMapping().get(indexOffsetAndStorageFlagEntry.getValue());
                byte[] indexData = storageClass.fetchDataFromIndex(storagePosition, INDEX_SIZE);

                int storageFlag;
                long physicalOffset;
                int msgSize;
                if (indexData == null) {
                    storageFlag = -1;
                    physicalOffset = storagePosition;
                    msgSize = INDEX_SIZE;
                } else {
                    ByteBuffer indexBuffer = ByteBuffer.wrap(indexData);
                    // 读取index数据
                    storageFlag = indexBuffer.getInt();
                    physicalOffset = indexBuffer.getLong();
                    msgSize = indexBuffer.getInt();
                }
                // TODO: 2022/10/26 和写数据的并发问题
                StorageFetchMsgResult storageFetchMsgResult
                        = StorageMapping.getMapping().get(storageFlag)
                        .fetchFromMsg(physicalOffset, msgSize, StorageConfig.getInstance().getMaxFetchMsgMb());
                fetchOffset = fetchIndex + (long) INDEX_SIZE * storageFetchMsgResult.getFetchMsgItemCount();

                return FetchResult.builder()
                        .fetchLastOffset(fetchOffset)
                        .noResult(noDataNew)
                        .fetchDataList(storageFetchMsgResult.getMsgDataList())
                        .build();
            } finally {
                lock.unlock();
            }
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
            StorageClass storageClass = StorageMapping.getMapping().get(putIndexEntry.getStoragePriorityFlag());
            StorageAppendResult appendResult = storageClass.appendIndex(indexBuf.array(), globalIndexWrote);
            if (!appendResult.getSuccess()) {
                log.error("追加index失败...");
                return;
            }
            globalIndexWrote += putIndexEntry.getMsgSize();
            Lock lock = INDEX_METADATA_LOCK.writeLock();
            lock.lock();
            try {
                boolean foundStorageFlag = false;
                for (Map.Entry<Long, Integer> indexAndStorageFlagEntry : INDEX_STORAGE_MAP.entrySet()) {
                    if (Objects.equals(indexAndStorageFlagEntry.getValue(), putIndexEntry.getStoragePriorityFlag())) {
                        foundStorageFlag = true;
                        break;
                    }
                }
                if (!foundStorageFlag) {
                    INDEX_STORAGE_MAP.put(globalIndexWrote, putIndexEntry.getStoragePriorityFlag());
                }
            } finally {
                lock.unlock();
            }
        }

        public Map.Entry<Long, Integer> getIndexMetadataEntry(Long index) {
            Lock lock = INDEX_METADATA_LOCK.readLock();
            lock.lock();
            try {
                return INDEX_STORAGE_MAP.lowerEntry(index);
            } finally {
                lock.unlock();
            }
        }

        public void updateIndexMetadata(Integer indexCount, Integer storageClassFlag) {
            Lock lock = INDEX_METADATA_LOCK.writeLock();
            lock.lock();
            try {
                for (Map.Entry<Long, Integer> indexAndStorageFlagEntry : INDEX_STORAGE_MAP.entrySet()) {
                    if (indexAndStorageFlagEntry.getValue().equals(storageClassFlag)) {
                        INDEX_STORAGE_MAP.remove(indexAndStorageFlagEntry.getKey());
                        INDEX_STORAGE_MAP.put(indexAndStorageFlagEntry.getKey() + indexCount * INDEX_SIZE, storageClassFlag);
                    }
                }
            } finally {
                lock.unlock();
            }
        }

    }

}
