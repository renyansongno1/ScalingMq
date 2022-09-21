package org.scalingmq.broker.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.server.cons.ScalingmqReqWrapper;

/**
 *
 * @author renyansong
 */
@ChannelHandler.Sharable
@Slf4j
public class BrokerProtoNetEventHandler extends SimpleChannelInboundHandler<ScalingmqReqWrapper.ScalingmqReq> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ScalingmqReqWrapper.ScalingmqReq msg) throws Exception {

    }

}
