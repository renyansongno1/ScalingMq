package org.scalingmq.broker.server.http.compress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import java.util.List;

/**
 * http的压缩处理
 * 会根据请求体的大小来判断是不是需要压缩
 * @author renyansong
 */
public class SmartHttpContentCompressor extends HttpContentCompressor {

    private boolean passThrough;

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        if (msg instanceof HttpResponse) {
            // Check if this response should be compressed
            HttpResponse res = (HttpResponse) msg;
            // by default compression is on (passThrough bypasses compression)
            passThrough = false;
            // If an "Content-Encoding: Identity" header was set, we do not compress
            if (res.headers().contains(HttpHeaderNames.CONTENT_ENCODING, HttpHeaderValues.IDENTITY, false)) {
                passThrough = true;
                // Remove header as one SHOULD NOT send Identity as content encoding.
                res.headers().remove(HttpHeaderNames.CONTENT_ENCODING);
            } else {
                // If the content type is not compressable (jpg, png ...), we skip compression
                String contentType = res.headers().get(HttpHeaderNames.CONTENT_TYPE);
                if (!MimeHelper.isCompressable(contentType)) {
                    passThrough = true;
                } else {
                    // If the content length is less than 1 kB but known, we also skip compression
                    int contentLength = 0;
                    String contentLengthStr = res.headers().get(HttpHeaderNames.CONTENT_LENGTH);
                    if (contentLengthStr != null && !"".equals(contentLengthStr)) {
                        contentLength = Integer.parseInt(contentLengthStr);
                    }
                    if (contentLength > 0 && contentLength < 1024) {
                        passThrough = true;
                    }
                }
            }
        }
        super.encode(ctx, msg, out);
    }

    @Override
    protected Result beginEncode(HttpResponse headers, String acceptEncoding) throws Exception {
        // If compression is skipped, we return null here which disables the compression effectively...
        if (passThrough) {
            return null;
        }
        return super.beginEncode(headers, acceptEncoding);
    }

}
