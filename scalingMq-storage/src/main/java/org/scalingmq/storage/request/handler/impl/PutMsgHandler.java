package org.scalingmq.storage.request.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.storage.aggr.MsgAggrService;
import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.storage.api.StorageApiResWrapper;
import org.scalingmq.storage.exception.ExceptionCodeEnum;
import org.scalingmq.storage.exception.StorageBaseException;
import org.scalingmq.storage.request.handler.RequestHandler;
import org.scalingmq.storage.request.handler.RequestHandlerFactory;

/**
 * 追加消息的请求处理器
 * @author renyansong
 */
@Slf4j
public class PutMsgHandler implements RequestHandler<StorageApiReqWrapper.StorageApiReq.PutMsgReq, StorageApiResWrapper.StorageApiRes> {

    @Override
    public StorageApiResWrapper.StorageApiRes handle(StorageApiReqWrapper.StorageApiReq.PutMsgReq putMsgReq) {
        try {
            StorageApiResWrapper.PutMsgRes putMsgRes = MsgAggrService.getInstance().putMsg(putMsgReq);
            return StorageApiResWrapper.StorageApiRes.newBuilder()
                    .setPutMsgRes(putMsgRes)
                    .build();
        } catch (StorageBaseException e) {
            log.warn("put msg exception, req:{}", putMsgReq, e);
            return StorageApiResWrapper.StorageApiRes.newBuilder()
                    .setErrorMsg(e.getMsg())
                    .setErrorCode(e.getCode().getCode())
                    .build();
        } catch (Exception e) {
            log.warn("put msg unknown exception, req:{}", putMsgReq, e);
            return StorageApiResWrapper.StorageApiRes.newBuilder()
                    .setErrorMsg(e.getMessage())
                    .setErrorCode(ExceptionCodeEnum.UNKNOWN.getCode())
                    .build();
        }
    }

}
