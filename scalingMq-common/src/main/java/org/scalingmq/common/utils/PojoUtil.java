package org.scalingmq.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * pojo的工具类
 * 反射的方式 不适用于超高性能要求的模式
 * @author renyansong
 */
@Slf4j
public class PojoUtil {

    private PojoUtil() {}

    /**
     * map 转 pojo
     */
    public static <T> T mapToObject(Map<String, String> map, Class<T> beanClass) {
        if (map == null) {
            return null;
        }

        try {
            Object obj = beanClass.getDeclaredConstructor().newInstance();

            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }

                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }

            return (T) obj;
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
            if(obj == null){
                return null;
            }

            Map<String, String> map = new HashMap<>();

            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                map.put(field.getName(), String.valueOf(field.get(obj)));
            }
            return map;
        } catch (Exception e) {
            log.error("obj to map error", e);
            return null;
        }
    }

}
