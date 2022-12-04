package org.scalingmq.storage.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.scalingmq.common.ioc.IocContainer;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.cons.StorageAppendResult;
import org.scalingmq.storage.core.storage.PartitionMsgStorage;
import org.scalingmq.storage.core.storage.entity.StorageFetchMsgResult;
import org.scalingmq.storage.core.storage.impl.DirectBufferStorage;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class DirectBufferStorageTest {

    private static final String MAGIC = "SCMQ";

    @BeforeAll
    public static void initTest() {
        log.info("init direct buffer storage test");
        IocContainer.getInstance().add(new PartitionMsgStorage());
    }

    /**
     * 单条消息追加测试
     */
    @Test
    void appendTest() {
        DirectBufferStorage directBufferStorage = new DirectBufferStorage();
        directBufferStorage.componentStart();

        String msg = "testAppendMsg";
        byte[] msgBody = msg.getBytes(StandardCharsets.UTF_8);
        // 存储数据
        appendTestMsg(msg, directBufferStorage);

        // 查询消息
        StorageFetchMsgResult storageFetchMsgResult = directBufferStorage.fetchFromMsg(0, 16, StorageConfig.getInstance().getMaxFetchMsgMb());
        List<byte[]> msgDataList = storageFetchMsgResult.getMsgDataList();
        Assertions.assertNotNull(msgDataList);
        byte[] bytes = msgDataList.get(0);

        ByteBuffer result = ByteBuffer.wrap(bytes);
        Assertions.assertEquals(result.getInt(), msgBody.length);
        byte[] magicByte = new byte[MAGIC.getBytes(StandardCharsets.UTF_8).length];
        result.get(magicByte);
        Assertions.assertArrayEquals(magicByte, MAGIC.getBytes(StandardCharsets.UTF_8));
        byte[] body = new byte[msgBody.length];
        result.get(body);
        Assertions.assertArrayEquals(body, msgBody);
    }

    /**
     * 多条消息追加
     */
    @Test
    void multiMsgAppendTest() {
        DirectBufferStorage directBufferStorage = new DirectBufferStorage();
        directBufferStorage.componentStart();

        List<byte[]> appendMsgBodyList = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            String msgBody = "multiMsgAppendTest-" + i;
            byte[] msgBodyBytes = msgBody.getBytes(StandardCharsets.UTF_8);
            appendTestMsg(msgBody, directBufferStorage);
            appendMsgBodyList.add(msgBodyBytes);
        }

        // 查询消息
        StorageFetchMsgResult storageFetchMsgResult = directBufferStorage.fetchFromMsg(0, 16, StorageConfig.getInstance().getMaxFetchMsgMb());
        List<byte[]> msgDataList = storageFetchMsgResult.getMsgDataList();
        Assertions.assertNotNull(msgDataList);
        for (int i = 0; i < msgDataList.size(); i++) {
            byte[] readResultBytes = msgDataList.get(i);
            byte[] writeMsgBytes = appendMsgBodyList.get(i);

            ByteBuffer result = ByteBuffer.wrap(readResultBytes);
            Assertions.assertEquals(result.getInt(), writeMsgBytes.length);
            byte[] magicByte = new byte[MAGIC.getBytes(StandardCharsets.UTF_8).length];
            result.get(magicByte);
            Assertions.assertArrayEquals(magicByte, MAGIC.getBytes(StandardCharsets.UTF_8));
            byte[] body = new byte[writeMsgBytes.length];
            result.get(body);
            Assertions.assertArrayEquals(body, writeMsgBytes);
        }
    }

    /**
     * 多条消息追加 从中间读取
     */
    @Test
    void multiAppendThenReadFromMidTest() {
        DirectBufferStorage directBufferStorage = new DirectBufferStorage();
        directBufferStorage.componentStart();

        List<byte[]> appendMsgBodyList = new ArrayList<>(10);

        for (int i = 0; i < 10; i++) {
            String msgBody = "multiAppendReadFromMidTest-" + i;
            byte[] msgBodyBytes = msgBody.getBytes(StandardCharsets.UTF_8);
            appendTestMsg(msgBody, directBufferStorage);
            if (i > 1) {
                appendMsgBodyList.add(msgBodyBytes);
            }
        }
        // 查询消息 忽略前面的两条消息
        StorageFetchMsgResult storageFetchMsgResult = directBufferStorage.fetchFromMsg(16 * 2, 16, StorageConfig.getInstance().getMaxFetchMsgMb());
        List<byte[]> msgDataList = storageFetchMsgResult.getMsgDataList();
        Assertions.assertNotNull(msgDataList);
        for (int i = 0; i < msgDataList.size(); i++) {
            byte[] readResultBytes = msgDataList.get(i);
            byte[] writeMsgBytes = appendMsgBodyList.get(i);

            ByteBuffer result = ByteBuffer.wrap(readResultBytes);
            Assertions.assertEquals(result.getInt(), writeMsgBytes.length);
            byte[] magicByte = new byte[MAGIC.getBytes(StandardCharsets.UTF_8).length];
            result.get(magicByte);
            Assertions.assertArrayEquals(magicByte, MAGIC.getBytes(StandardCharsets.UTF_8));
            byte[] body = new byte[writeMsgBytes.length];
            result.get(body);
            Assertions.assertArrayEquals(body, writeMsgBytes);
        }
    }

    private void appendTestMsg(String msgBodyStr, DirectBufferStorage directBufferStorage) {
        // 存储数据
        byte[] msgBody = msgBodyStr.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4 + MAGIC.getBytes(StandardCharsets.UTF_8).length + msgBody.length);
        buffer.putInt(msgBody.length);
        buffer.put(MAGIC.getBytes(StandardCharsets.UTF_8));
        buffer.put(msgBody);
        StorageAppendResult append = directBufferStorage.append(buffer.array());
        log.info("append result:{}", append);
        Assertions.assertTrue(append.getSuccess());

        // 存储索引
        ByteBuffer indexBuf = ByteBuffer.allocate(16);
        indexBuf.putInt(directBufferStorage.storagePriority());
        indexBuf.putLong(append.getOffset());
        indexBuf.putInt(msgBody.length);
        StorageAppendResult appendIndex = directBufferStorage.appendIndex(indexBuf.array(), 0);
        log.info("append index result:{}", appendIndex);
        Assertions.assertTrue(appendIndex.getSuccess());
    }

}
