package org.scalingmq.storage.request.handler.impl;

import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.storage.request.handler.RequestHandler;
import org.scalingmq.storage.request.handler.RequestHandlerFactory;

/**
 * 追加消息的请求处理器
 * @author renyansong
 */
public class PutMsgHandler implements RequestHandler<StorageApiReqWrapper.StorageApiReq.PutMsgReq, StorageApiResWrapper.PutMsgRes> {

    public PutMsgHandler() {
        RequestHandlerFactory.getInstance().addHandler(this);
    }

    @Override
    public StorageApiResWrapper.PutMsgRes handle(StorageApiReqWrapper.StorageApiReq.PutMsgReq putMsgReq) {
        return null;
    }

}
