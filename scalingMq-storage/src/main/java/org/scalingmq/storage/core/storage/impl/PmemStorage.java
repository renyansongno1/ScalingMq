package org.scalingmq.storage.core.storage.impl;

import com.intel.pmem.llpl.Transaction;
import com.intel.pmem.llpl.TransactionalHeap;
import com.intel.pmem.llpl.TransactionalMemoryBlock;
import org.scalingmq.common.ioc.IocContainer;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.storage.PartitionMsgStorage;
import org.scalingmq.storage.core.storage.StorageClass;
import org.scalingmq.storage.core.cons.StorageAppendResult;
import java.io.File;

/**
 * 持久内存存储实现
 * @author renyansong
 */
public class PmemStorage implements StorageClass {

    private TransactionalHeap msgHeap = null;

    private TransactionalHeap indexHeap = null;

    private long pmemMsgSize = 0L;

    private long pmemIndexSize = 0L;

    private long wrote = 0L;

    private long indexWrote = 0L;

    public PmemStorage() {
    }

    private void init() {
        String pmemMountPath = StorageConfig.getInstance().getPmemMountPath();
        if (pmemMountPath == null || "".equals(pmemMountPath)) {
            return;
        }
        pmemMountPath += StorageConfig.getInstance().getPartitionFileName();

        File file = new File(pmemMountPath);
        long pmemTotalSize = file.getTotalSpace();

        pmemIndexSize = pmemTotalSize * StorageConfig.getInstance().getIndexSpaceRatio() / 100;
        pmemMsgSize = pmemTotalSize - pmemIndexSize;

        // 构造持久内存
        msgHeap = TransactionalHeap.exists(pmemMountPath)
                ? TransactionalHeap.openHeap(pmemMountPath)
                : TransactionalHeap.createHeap(pmemMountPath, pmemMsgSize);

        indexHeap = TransactionalHeap.exists(pmemMountPath)
                ? TransactionalHeap.openHeap(pmemMountPath)
                : TransactionalHeap.createHeap(pmemMountPath, pmemIndexSize);


        IocContainer.getInstance().getObj(PartitionMsgStorage.class).addStorageClass(storagePriority(), this);
    }

    @Override
    public int storagePriority() {
        return 10;
    }

    @Override
    public StorageAppendResult append(byte[] msgBody) {
        if (wrote + msgBody.length > pmemMsgSize) {
            return StorageAppendResult.builder()
                    .success(false)
                    .build();
        }
        TransactionalMemoryBlock block = msgHeap.allocateMemoryBlock(256);
        Transaction.create(msgHeap, () -> {
            block.copyFromArray(msgBody, 0, 0, msgBody.length);
        });
        wrote += msgBody.length;
        return StorageAppendResult.builder()
                .success(true)
                .offset(wrote)
                .build();
    }

    @Override
    public StorageAppendResult appendIndex(byte[] indexBody) {
        if (indexWrote + indexBody.length > pmemIndexSize) {
            return StorageAppendResult.builder()
                    .success(false)
                    .build();
        }
        TransactionalMemoryBlock block = indexHeap.allocateMemoryBlock(256);
        Transaction.create(indexHeap, () -> {
            block.copyFromArray(indexBody, 0, 0, indexBody.length);
        });
        indexWrote += indexBody.length;
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

    }
}
