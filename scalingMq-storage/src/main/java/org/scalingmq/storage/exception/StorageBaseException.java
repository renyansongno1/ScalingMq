package org.scalingmq.storage.exception;

import lombok.Getter;

/**
 * 异常基类
 * @author renyansong
 */
@Getter
public class StorageBaseException extends RuntimeException {

    private final ExceptionCodeEnum code;

    private final String msg;

    public StorageBaseException(ExceptionCodeEnum code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

}
