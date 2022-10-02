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
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.broker.conf.BrokerConfig;
import org.scalingmq.broker.server.handler.BrokerHttpNetEventHandler;
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
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());
                            p.addLast("httpAggregator", new HttpObjectAggregator(1024 * 1024));
                            p.addLast("decompressor", new HttpContentDecompressor());
                            p.addLast("responseCompressor", new SmartHttpContentCompressor());
                            p.addLast("httpServiceHandler", BrokerHttpNetEventHandler.getInstance());
                        }
                    })
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture f = bootstrap.bind(BrokerConfig.HTTP_PORT).sync();
            f.channel().closeFuture().sync();
            stop();
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
        new Thread(this::start).start();
    }

    @Override
    public void componentStop() {
        stop();
    }

}
