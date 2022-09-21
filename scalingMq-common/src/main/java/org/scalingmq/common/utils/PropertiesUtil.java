package org.scalingmq.common.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * properties读取的工具类
 * @author renyansong
 */
public class PropertiesUtil {

    private final static Map<String, Properties> CONF_MAP = new HashMap<>();

    private PropertiesUtil() {
        throw new IllegalArgumentException("not support reflect invoke");
    }

    /**
     * 根据clazz的物理路径，获取propertyFileName名称的属性文件的Properties对象
     */
    public static void generateProperties(Class clazz, String propertyFileName, String configName) {
        InputStreamReader reader= new InputStreamReader(Objects.requireNonNull(clazz.getClassLoader().getResourceAsStream(propertyFileName)));
        Properties p = new Properties();
        try {
            p.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CONF_MAP.put(configName, p);
    }

    /**
     * 通过配置名称获取配置
     * @param configName 配置名称
     * @return properties
     */
    public static Properties getProperties(String configName) {
        return CONF_MAP.get(configName);
    }

}
