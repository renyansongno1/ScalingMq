package org.scalingmq.storage.request.handler;

import io.netty.channel.Channel;

/**
 * 请求处理器接口
 * @author renyansong
 */
public interface RequestHandler<REQ> {

    /**
     * 处理数据接口
     * @param req 请求数据
     * @return 响应数据
     */
    void handle(REQ req, Channel channel);

}
