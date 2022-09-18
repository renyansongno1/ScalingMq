package org.scalingmq.storage.exception;

/**
 * 没有找到对应的api处理器异常
 * @author renyansong
 */
public class NotFoundRequestHandlerException extends RuntimeException {

    @Override
    public String getMessage() {
        return "NotFoundRequestHandlerException please check api request";
    }

}
