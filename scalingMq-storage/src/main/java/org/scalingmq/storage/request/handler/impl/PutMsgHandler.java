package org.scalingmq.storage.request.handler.impl;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.storage.aggr.MsgAggrService;
import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.storage.request.handler.RequestHandler;

/**
 * 追加消息的请求处理器
 * @author renyansong
 */
@Slf4j
public class PutMsgHandler implements RequestHandler<StorageApiReqWrapper.StorageApiReq.PutMsgReq> {

    @Override
    public void handle(StorageApiReqWrapper.StorageApiReq.PutMsgReq putMsgReq, Channel channel) {
        MsgAggrService.getInstance().putMsg(putMsgReq, channel);
    }

}
