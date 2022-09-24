package org.scalingmq.storage.core.storage.impl;

import org.scalingmq.common.ioc.IocContainer;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.PartitionMsgStorage;
import org.scalingmq.storage.core.StorageClass;
import org.scalingmq.storage.core.cons.StorageAppendResult;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 磁盘存储介质实现
 * 包括 HDD SSD
 * @author renyansong
 */
public class DiskStorage implements StorageClass {

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
        IocContainer.getInstance().getObj(PartitionMsgStorage.class).addStorageClass(storagePriority(), this);
    }

    @Override
    public int storagePriority() {
        return 99;
    }

    @Override
    public StorageAppendResult append(byte[] msgBody) {
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
    public StorageAppendResult appendIndex(byte[] indexBody) {
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
    public void componentStart() {
        init(StorageConfig.getInstance().getDiskMountPath());
    }

    @Override
    public void componentStop() {
        try {
            msgDataFileChannel.force(true);
            indexFileChannel.force(true);
        } catch (IOException e) {
            // ignore
        }
    }
}
