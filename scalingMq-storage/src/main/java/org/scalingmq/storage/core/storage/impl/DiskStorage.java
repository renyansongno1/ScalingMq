package org.scalingmq.storage.core.storage.impl;

import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.PartitionMsgStorage;
import org.scalingmq.storage.core.StorageClass;
import org.scalingmq.storage.core.cons.StorageAppendResult;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.LongAdder;

/**
 * 磁盘存储介质实现
 * 包括 HDD SSD
 * @author renyansong
 */
public class DiskStorage implements StorageClass {

    private FileChannel fileChannel = null;

    private MappedByteBuffer mappedByteBuffer = null;

    private long maxFileSize = 0L;

    private final LongAdder wrote = new LongAdder();

    public DiskStorage() {
    }

    /**
     * 初始化
     */
    public void init(String storagePath) {
        try {
            File file = new File(storagePath + StorageConfig.getInstance().getPartitionFileName());
            maxFileSize = file.getTotalSpace();
            fileChannel = new RandomAccessFile(file, "rw").getChannel();
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileChannel.size());
        } catch (IOException e) {
            // ignore
            return;
        }
        PartitionMsgStorage.getInstance().addStorageClass(storagePriority(), this);
    }

    @Override
    public int storagePriority() {
        return 99;
    }

    @Override
    public StorageAppendResult append(byte[] msgBody) {
        // TODO: 2022/9/18 4k对齐
        if (wrote.intValue() + msgBody.length > maxFileSize) {
            return StorageAppendResult.builder()
                    .success(false)
                    .build();
        }
        mappedByteBuffer.put(msgBody);
        mappedByteBuffer.force();
        wrote.add(msgBody.length);
        return StorageAppendResult.builder()
                .success(true)
                .offset(wrote.longValue())
                .build();
    }

    @Override
    public void componentStart() {
        init(StorageConfig.getInstance().getDiskMountPath());
    }

    @Override
    public void componentStop() {
        try {
            fileChannel.force(true);
        } catch (IOException e) {
            // ignore
        }
    }
}
