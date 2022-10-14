package org.scalingmq.storage.request.handler.impl;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.storage.aggr.MsgAggrService;
import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.storage.request.handler.RequestHandler;

/**
 * 拉取消息的处理器
 * @author renyansong
 */
@Slf4j
public class FetchMsgHandler implements RequestHandler<StorageApiReqWrapper.StorageApiReq.FetchMsgReq> {

    @Override
    public void handle(StorageApiReqWrapper.StorageApiReq.FetchMsgReq fetchMsgReq, Channel channel) {
        MsgAggrService.getInstance().fetchMsg(fetchMsgReq);
    }

}
