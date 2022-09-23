package org.scalingmq.common.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * pojo的工具类
 * 反射的方式 不适用于超高性能要求的模式
 * @author renyansong
 */
@Slf4j
public class PojoUtil {

    private static final Gson GSON = new Gson();

    private PojoUtil() {}

    /**
     * map 转 pojo
     */
    public static <T> T mapToObject(Map<String, String> map, Class<T> beanClass) {
        try {
            return GSON.fromJson(GSON.toJson(map), beanClass);
        } catch (Exception e) {
            log.error("map error", e);
            return null;
        }
    }

    /**
     * pojo 转 map
     */
    public static Map<String, String> objectToMap(Object obj) {
        try {
            return GSON.fromJson(GSON.toJson(obj), new TypeToken<Map<String,String>>(){}.getType());
        } catch (Exception e) {
            log.error("obj to map error", e);
            return null;
        }
    }

}
