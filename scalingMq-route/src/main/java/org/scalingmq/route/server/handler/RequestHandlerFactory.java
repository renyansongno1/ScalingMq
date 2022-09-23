package org.scalingmq.route.server.handler;

import org.scalingmq.route.server.handler.impl.PutTopicMetadataHandler;
import org.scalingmq.route.client.entity.*;

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
            Type[] types = handler.getClass().getGenericInterfaces();
            Type type = types[0];
            if (type instanceof ParameterizedType parameterizedType) {
                if (parameterizedType.getActualTypeArguments()[0].equals(clazz)) {
                    return handler;
                }
            }
        }
        return null;
    }

}
