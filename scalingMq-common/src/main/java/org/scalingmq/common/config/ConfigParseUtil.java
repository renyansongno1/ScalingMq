package org.scalingmq.common.config;

import java.lang.reflect.Field;

/**
 * 配置解析工具
 * @author renyansong
 */
public class ConfigParseUtil {

    private static final ConfigParseUtil INSTANCE = new ConfigParseUtil();

    private ConfigParseUtil() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static ConfigParseUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 解析配置对象
     * @param config 配置对象
     */
    public void parse(Object config) {
        Class<?> configClass = config.getClass();
        Field[] fields = configClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            EnvironmentVariable environmentVariable = field.getAnnotation(EnvironmentVariable.class);
            if (environmentVariable != null) {
                String envKey;
                String value = environmentVariable.value();
                if (value == null || "".equals(value)) {
                    // 取字段名
                    envKey = field.getName();
                } else {
                    envKey = value;
                }
                String envValue = System.getenv(envKey);
                if (envValue != null && !"".equals(envValue)) {
                    try {
                        field.set(config, envValue);
                    } catch (IllegalAccessException e) {
                        // ignore
                    }
                }
            }
            // TODO: 2022/9/19 other config case
        }
    }

}
