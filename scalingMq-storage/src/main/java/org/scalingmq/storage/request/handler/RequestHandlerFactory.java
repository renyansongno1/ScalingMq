package org.scalingmq.storage.request.handler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 请求处理器的工厂
 * @author renyansong
 */
public class RequestHandlerFactory {

    private static final RequestHandlerFactory INSTANCE = new RequestHandlerFactory();

    private static final List<RequestHandler> HANDLERS = new ArrayList<>();

    private RequestHandlerFactory() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static RequestHandlerFactory getInstance() {
        return INSTANCE;
    }

    /**
     * 添加一个handler
     * thread safe
     * @param requestHandler 请求处理器
     */
    public void addHandler(RequestHandler requestHandler) {
        synchronized (this) {
            HANDLERS.add(requestHandler);
        }
    }

    /**
     * 通过输入的request类型 找到对应的handler处理器
     * @param clazz request class
     * @return handler
     */
    public RequestHandler getHandler(Class clazz) {
        // TODO: 2022/9/18 cache support
        for (RequestHandler handler : HANDLERS) {
            Type type = handler.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                System.out.println(parameterizedType.getActualTypeArguments()[0]);
                if (parameterizedType.getActualTypeArguments()[0].equals(clazz)) {
                    return handler;
                }
            }
        }
        return null;
    }

}
