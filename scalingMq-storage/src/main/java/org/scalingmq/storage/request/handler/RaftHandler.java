package org.scalingmq.storage.request.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.ioc.IocContainer;
import org.scalingmq.storage.core.replicate.raft.RaftCore;
import org.scalingmq.storage.core.replicate.raft.entity.RaftReqWrapper;
import org.scalingmq.storage.core.replicate.raft.entity.RaftResWrapper;

/**
 * Raft网络事件处理器
 *
 * @author renyansong
 */
@ChannelHandler.Sharable
@Slf4j
public class RaftHandler extends SimpleChannelInboundHandler<RaftReqWrapper.RaftReq> {

    @SuppressWarnings("AlibabaSwitchStatement")
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RaftReqWrapper.RaftReq msg) {
        switch (msg.getReqTypeValue()) {
            case RaftReqWrapper.RaftReq.ReqType.VOTE_VALUE -> {
                log.debug("收到选票请求:{}", msg);
                RaftResWrapper.RaftRes raftRes = IocContainer.getInstance().getObj(RaftCore.class).receiveVote(msg.getVoteReq());
                log.debug("发送选票响应:{}", raftRes.toString());
                ctx.channel().writeAndFlush(raftRes);
            }
            case RaftReqWrapper.RaftReq.ReqType.HEARTBEAT_VALUE -> {
                log.debug("收到心跳请求:{}", msg);
                RaftResWrapper.RaftRes raftRes = IocContainer.getInstance().getObj(RaftCore.class).receiveHeartbeat(msg.getHeartbeatReq());
                log.debug("发送心跳响应:{}", raftRes.toString());
                ctx.channel().writeAndFlush(raftRes);
            }
            default -> {
                log.warn("收到未知消息类型:{}", msg);
            }
        }
    }

}
