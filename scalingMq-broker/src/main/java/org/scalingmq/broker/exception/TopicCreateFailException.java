package org.scalingmq.broker.exception;

/**
 * topic已存在的异常
 * @author renyansong
 */
public class TopicCreateFailException extends Exception {

    private static final String MSG = "TopicCreateFail";

    public TopicCreateFailException() {
        super(MSG);
    }

    @Override
    public String getMessage() {
        return MSG;
    }
}
