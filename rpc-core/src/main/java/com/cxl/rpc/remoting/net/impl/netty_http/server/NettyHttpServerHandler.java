package com.cxl.rpc.remoting.net.impl.netty_http.server;

import com.cxl.rpc.remoting.net.params.Beat;
import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.remoting.net.params.RpcResponse;
import com.cxl.rpc.remoting.provider.RpcProviderFactory;
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
import java.util.concurrent.ThreadPoolExecutor;

public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServerHandler.class);

    private RpcProviderFactory rpcProviderFactory;
    private ThreadPoolExecutor serverHandlerPool;

    public NettyHttpServerHandler(final RpcProviderFactory rpcProviderFactory, final ThreadPoolExecutor serverHandlerPool) {
        this.rpcProviderFactory = rpcProviderFactory;
        this.serverHandlerPool = serverHandlerPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        final byte[] repuestBytes = ByteBufUtil.getBytes(msg.content());
        final String uri = msg.uri();

        final boolean keepAlive = HttpUtil.isKeepAlive(msg);

        serverHandlerPool.execute(new Runnable() {
            @Override
            public void run() {
                process(ctx, uri, repuestBytes, keepAlive);
            }
        });
    }

    private void process(ChannelHandlerContext ctx, String uri, byte[] repuestBytes, boolean keepAlive) {
        String repuestId = null;
        try {
            if ("/services".equals(uri)) {
                StringBuffer stringBuffer = new StringBuffer("<ui>");
                for (String serverKey : rpcProviderFactory.getServiceData().keySet()) {
                    stringBuffer.append("<li>").append(serverKey).append(": ").append(rpcProviderFactory.getServiceData().get(serverKey)).append("</li>");
                }
                stringBuffer.append("</ui>");

                byte[] responseBytes = stringBuffer.toString().getBytes("UTF-8");
                writeResponse(ctx, keepAlive, responseBytes);
            } else {
                if (repuestBytes.length == 0) {
                    throw new RpcException(">>>>>>request data empty");
                }

                RpcRequest request = (RpcRequest) rpcProviderFactory.getSerializer().deserializer(repuestBytes, RpcRequest.class);
                repuestId = request.getRequestId();


                if (Beat.BEAT_ID.equalsIgnoreCase(request.getRequestId())) {
                    LOGGER.debug(">>>>>>>>>>> provider netty_http server read beat-ping.");
                    return;
                }

                RpcResponse response = rpcProviderFactory.invokeService(request);
                byte[] responseBytes = rpcProviderFactory.getSerializer().serializer(response);
                writeResponse(ctx, keepAlive, responseBytes);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            RpcResponse response = new RpcResponse();
            response.setRequestId(repuestId);
            response.setErrorMsg(ThrowableUtil.toString(e));

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
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
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

