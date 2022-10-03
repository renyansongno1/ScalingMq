package org.scalingmq.demo.server;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Broekr的http协议网络事件处理者
 * @author renyansong
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpNetEventHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final HttpNetEventHandler INSTANCE = new HttpNetEventHandler();

    private static final Gson GSON = new Gson();

    /**
     * k -> URL
     * v -> 需要调用的方法
     */
    private static final Map<String, Map<Method, Object>> URL_MAPPING = new HashMap<>();

    private HttpNetEventHandler() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static HttpNetEventHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 添加映射
     * @param url url
     * @param methodObjectMap 方法和对应的对象
     */
    public void addUrlMapping(String url, Map<Method, Object> methodObjectMap) {
        URL_MAPPING.put(url, methodObjectMap);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        HttpMethod httpMethod = httpRequest.method();
        // 浏览器预检嗅探机制过滤
        if (httpMethod == HttpMethod.OPTIONS) {
            preCheckResponse(ctx);
            return;
        }

        // 找到对应的URL mapping
        // TODO: 2022/9/21 GET模式适配
        String path = httpRequest.uri();
        if (path.contains("?")) {
            path = path.split("\\?")[0];
        }

        Map<Method, Object> methodObjectMap = URL_MAPPING.get(path);
        if (methodObjectMap == null) {
            ctx.channel().writeAndFlush(new DefaultFullHttpResponse (HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND)).addListener(ChannelFutureListener.CLOSE);
            return;
        }

        // 读取http request中的数据
        String httpBodyContent = "";
        if (httpRequest.content().readableBytes() > 0) {
            httpBodyContent = httpRequest.content().toString(StandardCharsets.UTF_8);
        }
        for (Map.Entry<Method, Object> methodObjectEntry : methodObjectMap.entrySet()) {
            Method method = methodObjectEntry.getKey();
            Object obj = methodObjectEntry.getValue();
            // 解析成对应的参数
            for (Parameter parameter : method.getParameters()) {
                RequestBody annotation = parameter.getAnnotation(RequestBody.class);
                if (annotation != null) {
                    Class<?> parameterClass = parameter.getType();
                    Object param = GSON.fromJson(httpBodyContent, parameterClass);
                    Object invokeResult = method.invoke(obj, param);
                    ByteBuf buffer = ctx.alloc().buffer();
                    buffer.writeBytes(GSON.toJson(invokeResult).getBytes(StandardCharsets.UTF_8));

                    DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
                    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buffer.readableBytes());
                    // CORS
                    response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                    response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
                    response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
                    response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                    ctx.channel().writeAndFlush(response);
                    // 只支持一个参数
                    break;
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("http异常...", cause);
        ctx.channel().writeAndFlush(new DefaultFullHttpResponse (HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR)).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 预检跨域
     */
    private void preCheckResponse(ChannelHandlerContext ctx) {
        String ok = "ok";
        ByteBuf buffer = ctx.alloc().buffer(ok.getBytes(StandardCharsets.UTF_8).length);
        buffer.writeBytes(ok.getBytes(StandardCharsets.UTF_8));
        // 写数据回channel
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, buffer);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, ok.getBytes(StandardCharsets.UTF_8).length);
        // CORS
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        ctx.pipeline().channel().writeAndFlush(response);
    }

}
