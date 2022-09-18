package org.scalingmq.storage.core.storage.impl;

import com.intel.pmem.llpl.Transaction;
import com.intel.pmem.llpl.TransactionalHeap;
import com.intel.pmem.llpl.TransactionalMemoryBlock;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.PartitionMsgStorage;
import org.scalingmq.storage.core.StorageClass;
import org.scalingmq.storage.core.cons.StorageAppendResult;
import java.io.File;
import java.util.concurrent.atomic.LongAdder;

/**
 * 持久内存存储实现
 * @author renyansong
 */
public class PmemStorage implements StorageClass {

    private TransactionalHeap heap = null;

    private long pmemTotalSize = 0L;

    private final LongAdder wrote = new LongAdder();

    public PmemStorage() {
    }

    private void init() {
        String pmemMountPath = StorageConfig.getInstance().getPmemMountPath();
        if (pmemMountPath == null || "".equals(pmemMountPath)) {
            return;
        }
        pmemMountPath += StorageConfig.getInstance().getPartitionFileName();

        // 构造持久内存
        heap = TransactionalHeap.exists(pmemMountPath)
                ? TransactionalHeap.openHeap(pmemMountPath)
                : TransactionalHeap.createHeap(pmemMountPath);

        File file = new File(pmemMountPath);
        pmemTotalSize = file.getTotalSpace();

        PartitionMsgStorage.getInstance().addStorageClass(storagePriority(), this);
    }

    @Override
    public int storagePriority() {
        return 10;
    }

    @Override
    public StorageAppendResult append(byte[] msgBody) {
        if (wrote.intValue() + msgBody.length > pmemTotalSize) {
            return StorageAppendResult.builder()
                    .success(false)
                    .build();
        }
        TransactionalMemoryBlock block = heap.allocateMemoryBlock(256);
        Transaction.create(heap, () -> {
            block.copyFromArray(msgBody, 0, 0, msgBody.length);
        });
        wrote.add(msgBody.length);
        return StorageAppendResult.builder()
                .success(true)
                .offset(wrote.longValue())
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
