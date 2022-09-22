package org.scalingmq.broker.exception;

/**
 * topic已存在的异常
 * @author renyansong
 */
public class TopicAlreadyExistException extends Exception {

    private static final String MSG = "TopicAlreadyExist";

    public TopicAlreadyExistException() {
        super(MSG);
    }

    @Override
    public String getMessage() {
        return MSG;
    }
}
