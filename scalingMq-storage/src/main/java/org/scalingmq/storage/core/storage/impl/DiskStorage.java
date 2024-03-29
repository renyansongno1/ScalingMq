package org.scalingmq.storage.core.storage.impl;

import io.netty.util.internal.shaded.org.jctools.queues.SpscLinkedQueue;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.storage.StorageClass;
import org.scalingmq.storage.core.cons.StorageAppendResult;
import org.scalingmq.storage.core.storage.StorageMapping;
import org.scalingmq.storage.core.storage.entity.StorageFetchMsgResult;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Duration;

/**
 * 磁盘存储介质实现
 * 包括 HDD SSD
 * @author renyansong
 */
public class DiskStorage implements StorageClass {

    /**
     * 刷盘的基础大小
     */
    private static final int FLUSH_BASE_SIZE = 4 * 1024;

    /**
     * 累计中的数据
     */
    private final SpscLinkedQueue<Integer> accumulateDataQueue = new SpscLinkedQueue<>();

    /**
     * 启停标识
     */
    private static volatile boolean STOP = false;

    private FileChannel msgDataFileChannel = null;

    private MappedByteBuffer msgDataMappedByteBuffer = null;

    private FileChannel indexFileChannel = null;

    private MappedByteBuffer indexMappedByteBuffer = null;

    private long maxFileSize = 0L;

    private long wrote = 0L;

    private long indexWrote = 0L;

    public DiskStorage() {
    }

    /**
     * 初始化
     */
    public void init(String storagePath) {
        try {
            File file = new File(storagePath);
            maxFileSize = file.getTotalSpace();

            File msgDataFile = new File(storagePath + StorageConfig.getInstance().getPartitionFileName());
            msgDataFileChannel = new RandomAccessFile(msgDataFile, "rw").getChannel();
            msgDataMappedByteBuffer = msgDataFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, msgDataFileChannel.size());

            File indexFile = new File(storagePath + StorageConfig.getInstance().getPartitionIndexFileName());
            indexFileChannel = new RandomAccessFile(indexFile, "rw").getChannel();
            indexMappedByteBuffer = indexFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, indexFileChannel.size());
        } catch (Exception e) {
            // ignore
            return;
        }
        // 启动刷盘任务
        Thread.ofVirtual().start(new FlushDataTask());
        // 注册
        StorageMapping.addStorageClass(storagePriority(), this);
    }

    @Override
    public int storagePriority() {
        return 99;
    }

    @Override
    public StorageAppendResult append(byte[] msgBody) {
        // 收到数据先扔到队列进行累批
        accumulateDataQueue.add(msgBody.length);

        // TODO: 2022/9/18 4k对齐
        if (wrote + msgBody.length > maxFileSize) {
            return StorageAppendResult.builder()
                    .success(false)
                    .build();
        }
        msgDataMappedByteBuffer.put(msgBody);
        msgDataMappedByteBuffer.force();
        wrote += msgBody.length;
        return StorageAppendResult.builder()
                .success(true)
                .offset(wrote)
                .build();
    }

    @Override
    public StorageAppendResult appendIndex(byte[] indexBody, long globalIndexPosition) {
        if (indexWrote + indexBody.length > maxFileSize) {
            return StorageAppendResult.builder()
                    .success(false)
                    .build();
        }
        indexMappedByteBuffer.put(indexBody);
        indexMappedByteBuffer.force();
        indexWrote += indexBody.length;
        return StorageAppendResult.builder()
                .success(true)
                .offset(indexWrote)
                .build();
    }

    @Override
    public byte[] fetchDataFromIndex(long storagePosition, int indexSize) {
        return new byte[0];
    }

    @Override
    public StorageFetchMsgResult fetchFromMsg(long physicalOffset, int msgSize, String maxFetchMsgMb) {
        return null;
    }

    @Override
    public void componentStart() {
        init(StorageConfig.getInstance().getDiskMountPath());
    }

    @Override
    public void componentStop() {
        try {
            msgDataFileChannel.force(true);
            indexFileChannel.force(true);

            STOP = true;
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * 刷盘任务
     */
    private class FlushDataTask implements Runnable {

        @Override
        public void run() {
            Integer accumulateSize = 0;
            while (!STOP) {
                Integer size = null;
                while (size == null) {
                    size = accumulateDataQueue.poll();
                    if (size == null) {
                        try {
                            Thread.sleep(Duration.ofMillis(1));
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }
                accumulateSize += size;
                /*if (accumulateSize >)*/
            }
        }
    }

    /**
     * 数据文件
     */
    private class DataFile {



    }
}
