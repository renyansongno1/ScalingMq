package org.scalingmq.demo.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识要处理的URL
 * @author renyansong
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpEndpoint {

    /**
     * 要处理的url
     */
    String value();

}
