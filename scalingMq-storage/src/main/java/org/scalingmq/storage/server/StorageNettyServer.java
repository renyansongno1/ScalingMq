package org.scalingmq.storage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.common.lifecycle.Lifecycle;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.replicate.raft.entity.RaftReqWrapper;
import org.scalingmq.storage.request.handler.NetworkInBoundHandler;
import org.scalingmq.storage.request.handler.NetworkOutBoundHandler;
import org.scalingmq.storage.request.handler.RaftHandler;

/**
 * netty实现的存储层服务器
 * @author renyansong
 */
public class StorageNettyServer implements Lifecycle {

    private volatile boolean isClosing = false;

    private void start() {
        initEventLoop();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(determineServerSocketChannel())
                    .option(ChannelOption.SO_BACKLOG, 511)
                    .childHandler(new ChannelInitUseByPort())
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture f = bootstrap.bind(StorageConfig.RAFT_PORT).sync();
            ChannelFuture fs = bootstrap.bind(StorageConfig.MSG_PORT).sync();

            // 不阻塞
            f.channel().closeFuture().addListener((ChannelFutureListener) future -> future.channel().close());
            fs.channel().closeFuture().addListener((ChannelFutureListener) future -> future.channel().close());

        } catch (Throwable e) {
            stop();
        }
    }

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    private void initEventLoop() {
        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup(1, new DefaultThreadFactory("netty-boss"));
            workGroup = new EpollEventLoopGroup(4, new DefaultThreadFactory("netty-worker"));

        } else {
            bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("netty-boss"));
            workGroup = new NioEventLoopGroup(4, new DefaultThreadFactory("netty-worker"));
        }
    }

    private Class<? extends ServerChannel> determineServerSocketChannel() {
        if (Epoll.isAvailable()) {
            return EpollServerSocketChannel.class;
        }

        return NioServerSocketChannel.class;
    }

    public void stop() {
        if (!isClosing) {

            isClosing = true;

            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workGroup != null) {
                workGroup.shutdownGracefully();
            }
        }
    }

    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    @Override
    public void componentStart() {
        start();
    }

    @Override
    public void componentStop() {
        stop();
    }

    /**
     * 针对不同的port进行处理
     */
    private static class ChannelInitUseByPort extends ChannelInitializer<SocketChannel> {

        @SuppressWarnings("AlibabaSwitchStatement")
        @Override
        public void initChannel(SocketChannel ch) {
            // 端口判断
            switch (ch.localAddress().getPort()) {
                case StorageConfig.RAFT_PORT -> {
                    ChannelPipeline p = ch.pipeline();
                    // ----Protobuf处理器，这里的配置是关键----
                    p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());// 用于decode前解决半包和粘包问题（利用包头中的包含数组长度来识别半包粘包）
                    //配置Protobuf解码处理器，消息接收到了就会自动解码，ProtobufDecoder是netty自带的，Message是自己定义的Protobuf类
                    p.addLast("protobufDecoder",new ProtobufDecoder(RaftReqWrapper.RaftReq.getDefaultInstance()));
                    // 用于在序列化的字节数组前加上一个简单的包头，只包含序列化的字节长度。
                    p.addLast("frameEncoder",new ProtobufVarint32LengthFieldPrepender());
                    //配置Protobuf编码器，发送的消息会先经过编码
                    p.addLast("protobufEncoder", new ProtobufEncoder());
                    // ----Protobuf处理器END----
                    p.addLast("handler", new RaftHandler());
                }
                case StorageConfig.MSG_PORT  -> {
                    ChannelPipeline p = ch.pipeline();
                    // ----Protobuf处理器，这里的配置是关键----
                    p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());// 用于decode前解决半包和粘包问题（利用包头中的包含数组长度来识别半包粘包）
                    //配置Protobuf解码处理器，消息接收到了就会自动解码，ProtobufDecoder是netty自带的，Message是自己定义的Protobuf类
                    p.addLast("protobufDecoder",new ProtobufDecoder(StorageApiReqWrapper.StorageApiReq.getDefaultInstance()));
                    // 用于在序列化的字节数组前加上一个简单的包头，只包含序列化的字节长度。
                    p.addLast("frameEncoder",new ProtobufVarint32LengthFieldPrepender());
                    //配置Protobuf编码器，发送的消息会先经过编码
                    p.addLast("protobufEncoder", new NetworkOutBoundHandler());
                    // ----Protobuf处理器END----
                    p.addLast("handler", new NetworkInBoundHandler());
                }
                default -> throw new RuntimeException("unknown netty server port");
            }
        }

    }

}
