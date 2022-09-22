package org.scalingmq.route.server.handler;

/**
 * 请求处理器接口
 * @author renyansong
 */
public interface RequestHandler<REQ,RES> {

    /**
     * 处理数据接口
     * @param req 请求数据
     * @return 响应数据
     */
    RES handle(REQ req);

}
