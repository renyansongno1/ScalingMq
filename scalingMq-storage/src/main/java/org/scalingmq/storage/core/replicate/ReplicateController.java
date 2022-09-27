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



}
