package org.scalingmq.broker.exception;

/**
 * topic已存在的异常
 * @author renyansong
 */
public class MetadataFetchException extends Exception {

    public static final String MSG = "MetadataFetch";

    public MetadataFetchException() {
        super(MSG);
    }

    @Override
    public String getMessage() {
        return MSG;
    }
}
