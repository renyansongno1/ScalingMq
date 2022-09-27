package org.scalingmq.storage.aggr;

import lombok.extern.slf4j.Slf4j;

/**
 * 消息聚合业务层
 * @author renyansong
 */
@Slf4j
public class MsgAggrService {

    private static final MsgAggrService INSTANCE = new MsgAggrService();

    private MsgAggrService() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static MsgAggrService getInstance() {
        return INSTANCE;
    }

    public void putMsg() {

    }

}
