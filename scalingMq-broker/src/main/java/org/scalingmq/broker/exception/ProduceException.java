package org.scalingmq.broker.exception;

/**
 * topic已存在的异常
 * @author renyansong
 */
public class ProduceException extends Exception {

    public static final String MSG = "ProduceException";

    public ProduceException() {
        super(MSG);
    }

    @Override
    public String getMessage() {
        return MSG;
    }
}
