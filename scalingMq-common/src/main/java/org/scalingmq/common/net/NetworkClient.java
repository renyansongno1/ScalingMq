package org.scalingmq.common.net;

import com.google.protobuf.MessageLite;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 网络客户端实现
 * @author renyansong
 */
@Slf4j
public class NetworkClient {

    private static final NetworkClient INSTANCE = new NetworkClient();

    /**
     * 存储所有存在的连接
     */
    private static final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 存储没有响应的数据
     */
    private static final Map<String, Object> RES_MAP = new ConcurrentHashMap<>();

    private NetworkClient() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static NetworkClient getInstance() {
        return INSTANCE;
    }

    /**
     * 发送请求
     * @param req 请求
     * @param addr 地址
     * @param port 端口
     * @return 响应
     */
    public MessageLite sendReq(Object req, String addr, int port, MessageLite messageLite) {
        if (port == 0) {
            port = Integer.parseInt(addr.split(":")[1]);
        }
        String connection = addr + ":" + port;
        log.debug("network client 收到发送请求到:{}", connection);
        Channel channel = CHANNEL_MAP.get(connection);
        if (channel != null) {
            log.debug("network client 已缓存连接:{}", connection);
            // 已经连接过了
            Object obj = new Object();
            RES_MAP.put(channel.id().toString(), obj);
            synchronized (obj) {
                int finalPort = port;
                channel.writeAndFlush(req).addListener((ChannelFutureListener) f -> {
                    if (!f.isSuccess()) {
                        log.warn("发送数据失败.., addr:{}, port:{}", addr, finalPort);
                        RES_MAP.remove(channel.id().toString());
                    }
                });
                try {
                    log.debug("network client 开始等待remote响应:{}", connection);
                    obj.wait();
                } catch (InterruptedException e) {
                    // ignore
                    return null;
                }
            }
            // v值被替换
            MessageLite rst = (MessageLite) RES_MAP.get(channel.id().toString());
            log.debug("network client 收到远端:{},响应:{}", connection, rst.toString());
            // 清理map
            RES_MAP.remove(channel.id().toString());
            return rst;
        } else {
            // 建立连接
            log.debug("network client 开始创建连接:{}", connection);
            CountDownLatch waitConnected = new CountDownLatch(1);
            connect(addr, port, messageLite, waitConnected);
            try {
                boolean await = waitConnected.await(10000L, TimeUnit.MILLISECONDS);
                if (!await) {
                    // TODO: 2022/9/24 重连机制
                    log.error("network client 建立连接失败:{}", connection);
                    return null;
                }
            } catch (InterruptedException e) {
                // ignore
            }
            return sendReq(req, addr, port, messageLite);
        }
    }

    /**
     * 连接remote
     * @param addr remote addr
     * @param port remote port
     * @param messageLite 数据协议
     */
    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    private void connect(String addr, int port, MessageLite messageLite, CountDownLatch await) {
        String connection = addr + ":" + port;

        new Thread(new ConnectTask(addr, port, messageLite, await), connection + "-connection-thread").start();
    }

    /**
     * Netty client的连接任务
     */
    private record ConnectTask(String addr, int port, MessageLite prototype, CountDownLatch await) implements Runnable {

        @Override
        public void run() {
            String connection = addr + ":" + port;
            // 配置客户端NIO线程组
            EventLoopGroup group = new NioEventLoopGroup(1);
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(
                                new ChannelInitializer<SocketChannel>() {
                                    @Override
                                    public void initChannel(SocketChannel ch) {
                                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                                        ch.pipeline().addLast(new ProtobufDecoder(prototype));
                                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                                        ch.pipeline().addLast(new ProtobufEncoder());
                                        ch.pipeline().addLast(new EventHandler(await, connection));
                                    }
                                });

                // 发起异步连接操作
                ChannelFuture f = b.connect(addr, port).sync();

                // 当客户端链路关闭
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error("netty client error...", e);
            } finally {
                // 优雅退出，释放NIO线程组
                group.shutdownGracefully();
                CHANNEL_MAP.remove(connection);
            }
        }
    }

    /**
     * 处理网络事件的handler
     */
    private static class EventHandler extends ChannelInboundHandlerAdapter {

        private final CountDownLatch connectionWait;

        private final String connection;

        public EventHandler(CountDownLatch await, String connection) {
            this.connectionWait = await;
            this.connection = connection;
        }

        /**
         * 连接建立
         * @param ctx 上下文
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            // 设置连接
            CHANNEL_MAP.put(connection, ctx.channel());
            connectionWait.countDown();
        }

        /**
         * 连接丢失
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            CHANNEL_MAP.remove(connection);
        }

        /**
         * 读取到Server的响应
         * @param ctx 上下文
         * @param msg message lite对象
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            log.debug("客户端读取远端数据:{}, 类型:{}", msg, msg.getClass().getName());
            // 读取到服务端的返回值
            Object waitObj = RES_MAP.get(ctx.channel().id().toString());
            // 首先先上锁
            synchronized (waitObj) {
                // 修改map的value为真实的响应值
                RES_MAP.put(ctx.channel().id().toString(), msg);
                // 唤醒等待线程
                waitObj.notifyAll();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("连接中出现异常... connection:{}", connection, cause);
        }
    }

}
