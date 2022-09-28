package org.scalingmq.storage.request.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.storage.aggr.MsgAggrService;
import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.storage.api.StorageApiResWrapper;
import org.scalingmq.storage.exception.ExceptionCodeEnum;
import org.scalingmq.storage.exception.StorageBaseException;
import org.scalingmq.storage.request.handler.RequestHandler;

/**
 * 拉取消息的处理器
 * @author renyansong
 */
@Slf4j
public class FetchMsgHandler implements RequestHandler<StorageApiReqWrapper.StorageApiReq.FetchMsgReq, StorageApiResWrapper.StorageApiRes> {

    @Override
    public StorageApiResWrapper.StorageApiRes handle(StorageApiReqWrapper.StorageApiReq.FetchMsgReq fetchMsgReq) {
        try {
            StorageApiResWrapper.FetchMsgRes fetchMsgRes = MsgAggrService.getInstance().fetchMsg(fetchMsgReq);
            return StorageApiResWrapper.StorageApiRes.newBuilder()
                    .setFetchMsgRes(fetchMsgRes)
                    .build();
        } catch (StorageBaseException e) {
            log.warn("fetch msg exception, req:{}", fetchMsgReq, e);
            return StorageApiResWrapper.StorageApiRes.newBuilder()
                    .setErrorMsg(e.getMsg())
                    .setErrorCode(e.getCode().getCode())
                    .build();
        } catch (Exception e) {
            log.warn("fetch msg unknown exception, req:{}", fetchMsgReq, e);
            return StorageApiResWrapper.StorageApiRes.newBuilder()
                    .setErrorMsg(e.getMessage())
                    .setErrorCode(ExceptionCodeEnum.UNKNOWN.getCode())
                    .build();
        }
    }

}
