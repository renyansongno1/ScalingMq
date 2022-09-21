package org.scalingmq.broker.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.conf.BrokerConfig;
import org.scalingmq.broker.server.cons.ScalingmqReqWrapper;
import org.scalingmq.broker.server.handler.BrokerHttpNetEventHandler;
import org.scalingmq.broker.server.handler.BrokerProtoNetEventHandler;
import org.scalingmq.broker.server.http.compress.SmartHttpContentCompressor;
import org.scalingmq.common.lifecycle.Lifecycle;

/**
 * broker的server端netty实现
 *
 * @author renyansong
 */
@Slf4j
public class BrokerNettyServer implements Lifecycle {

    private volatile boolean isClosing = false;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    private void start() {
        initEventLoop();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(determineServerSocketChannel())
                    .option(ChannelOption.SO_BACKLOG, 511)
                    .childHandler(new ChannelInitUseByPort())
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture f = bootstrap.bind(BrokerConfig.HTTP_PORT).sync();
            ChannelFuture fs = bootstrap.bind(BrokerConfig.MSG_PORT).sync();

            // 不阻塞
            f.channel().closeFuture().addListener((ChannelFutureListener) future -> future.channel().close());
            fs.channel().closeFuture().addListener((ChannelFutureListener) future -> future.channel().close());

        } catch (Throwable e) {
            stop();
        }
    }

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
                case BrokerConfig.HTTP_PORT -> {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new HttpServerCodec());
                    p.addLast("httpAggregator", new HttpObjectAggregator(1024 * 1024));
                    p.addLast("decompressor", new HttpContentDecompressor());
                    p.addLast("responseCompressor", new SmartHttpContentCompressor());
                    p.addLast("httpServiceHandler", BrokerHttpNetEventHandler.getInstance());
                }
                case BrokerConfig.MSG_PORT  -> {
                    ChannelPipeline cp = ch.pipeline();
                    cp.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
                    cp.addLast("protobufDecoder", new ProtobufDecoder(ScalingmqReqWrapper.ScalingmqReq.getDefaultInstance()));
                    cp.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
                    cp.addLast("protobufEncoder", new ProtobufEncoder());
                    cp.addLast("handler", new BrokerProtoNetEventHandler());
                }
                default -> throw new RuntimeException("unknown netty server port");
            }
        }

    }

}
