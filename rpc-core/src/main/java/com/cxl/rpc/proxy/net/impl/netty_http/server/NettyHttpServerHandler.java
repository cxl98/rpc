package com.cxl.rpc.proxy.net.impl.netty_http.server;

import com.cxl.rpc.proxy.net.params.Beat;
import com.cxl.rpc.proxy.net.params.RpcRequest;
import com.cxl.rpc.proxy.net.params.RpcResponse;
import com.cxl.rpc.proxy.provider.RpcProviderFactory;
import com.cxl.rpc.util.RpcException;
import com.cxl.rpc.util.ThrowableUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author cxl
 */
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServerHandler.class);

    private final RpcProviderFactory rpcProviderFactory;
    private final ThreadPoolExecutor serverHandlerPool;
    private static final String SERVER="/services";

    public NettyHttpServerHandler(final RpcProviderFactory rpcProviderFactory, final ThreadPoolExecutor serverHandlerPool) {
        this.rpcProviderFactory = rpcProviderFactory;
        this.serverHandlerPool = serverHandlerPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        final byte[] requestBytes = ByteBufUtil.getBytes(msg.content());
        final String uri = msg.uri();

        final boolean keepAlive = HttpUtil.isKeepAlive(msg);

        serverHandlerPool.execute(() -> process(ctx, uri, requestBytes, keepAlive));
    }

    private void process(ChannelHandlerContext ctx, String uri, byte[] requestBytes, boolean keepAlive) {
        if (SERVER.equals(uri)) {
            StringBuilder stringBuffer = new StringBuilder("<ui>");
            for (String serverKey : rpcProviderFactory.getServiceData().keySet()) {
                stringBuffer.append("<li>").append(serverKey).append(": ").append(rpcProviderFactory.getServiceData().get(serverKey)).append("</li>");
            }
            stringBuffer.append("</ui>");

            byte[] responseBytes = stringBuffer.toString().getBytes(StandardCharsets.UTF_8);
            writeResponse(ctx, keepAlive, responseBytes);
        } else {
            if (requestBytes.length == 0) {
                throw new RpcException(">>>>>>request data empty");
            }

            RpcRequest request = (RpcRequest) rpcProviderFactory.getSerializer().deSerializer(requestBytes, RpcRequest.class);


            if (Beat.BEAT_ID.equalsIgnoreCase(request.getRequestId())) {
                LOGGER.debug(">>>>>>>>>>> provider netty_http server read beat-ping.");
                return;
            }

            RpcResponse response = rpcProviderFactory.invokeService(request);
            byte[] responseBytes = rpcProviderFactory.getSerializer().serializer(response);
            writeResponse(ctx, keepAlive, responseBytes);
        }
    }

    private void writeResponse(ChannelHandlerContext ctx, boolean keepAlive, byte[] responseBytes) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBytes));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        if (keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error(">>>>>>>>provider netty_http server caught exception", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
            LOGGER.debug(">>>>>>>> provider netty_http server close an idle channel");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}

