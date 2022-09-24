package org.scalingmq.common.ioc;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储所有的单例
 * @author renyansong
 */
public class IocContainer {

    private static final IocContainer INSTANCE = new IocContainer();

    private static final List<Object> CONTAINER = new ArrayList<>(256);

    private IocContainer(){
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static IocContainer getInstance() {
        return INSTANCE;
    }

    /**
     * 添加对象
     * @param o 要被存储的单例
     */
    public void add(Object o) {
        CONTAINER.add(o);
    }

    /**
     * 查询对应的实例bean
     * @param clazz bean 类型
     * @param <T> class的类型
     * @return 实例对象
     */
    public <T> T getObj(Class<T> clazz) {
        for (Object o : CONTAINER) {
            if (o.getClass().equals(clazz)) {
                return (T) o;
            }
        }
        return null;
    }

}
