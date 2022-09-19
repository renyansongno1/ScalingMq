package org.scalingmq.storage.core.replicate;

/**
 * 复制控制器
 * @author renyansong
 */
public class ReplicateController {

    private static final ReplicateController INSTANCE = new ReplicateController();

    private ReplicateController() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static ReplicateController getInstance() {
        return INSTANCE;
    }

    /**
     * 消息的复制控制
     * @param msgBody 消息数据体
     * @return 消息追加后的物理偏移量
     */
    public long replicate(byte[] msgBody) {

        return 0L;
    }

}
