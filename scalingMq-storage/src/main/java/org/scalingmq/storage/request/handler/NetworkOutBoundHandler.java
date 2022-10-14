package org.scalingmq.storage.request.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.scalingmq.storage.api.StorageApiResWrapper;

/**
 * 出流量控制
 * @author renyansong
 */
public class NetworkOutBoundHandler extends ProtobufEncoder {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // 对消息按类型进行封装
        StorageApiResWrapper.StorageApiRes res;
        if (msg instanceof StorageApiResWrapper.FetchMsgRes) {
            res = StorageApiResWrapper.StorageApiRes.newBuilder()
                    .setFetchMsgRes((StorageApiResWrapper.FetchMsgRes) msg)
                    .build();
        } else if (msg instanceof StorageApiResWrapper.PutMsgRes) {
            res = StorageApiResWrapper.StorageApiRes.newBuilder()
                    .setPutMsgRes((StorageApiResWrapper.PutMsgRes) msg)
                    .build();
        } else {
            throw new RuntimeException("unknown Response msg type:" + msg.getClass().getName());
        }

        super.write(ctx, res, promise);
    }

}
