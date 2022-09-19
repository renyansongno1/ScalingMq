package org.scalingmq.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 取值环境变量
 * @author renyansong
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnvironmentVariable {

    /**
     * 定义环境变量key
     * @return 环境变量key
     */
    String value() default "";

}
