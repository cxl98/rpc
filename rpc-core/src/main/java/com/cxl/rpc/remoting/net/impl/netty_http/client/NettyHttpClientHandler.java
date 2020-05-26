package com.cxl.rpc.remoting.net.impl.netty_http.client;

import com.cxl.rpc.remoting.consumer.RpcInvokerFactory;
import com.cxl.rpc.remoting.net.params.Beat;
import com.cxl.rpc.remoting.net.params.RpcResponse;
import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.RpcException;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyHttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private static final Logger LOGGER= LoggerFactory.getLogger(NettyHttpClientHandler.class);

    private RpcInvokerFactory rpcInvokerFactory;
    private Serializer serializer;
    private NettyHttpConnectClient nettyHttpConnectClient;


    public NettyHttpClientHandler(final RpcInvokerFactory rpcInvokerFactory, Serializer serializer, final NettyHttpConnectClient connectClient) {
        this.rpcInvokerFactory=rpcInvokerFactory;
        this.serializer=serializer;
        this.nettyHttpConnectClient=connectClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        if (!HttpResponseStatus.OK.equals(msg.status())) {
            throw new RpcException(">>>>>response status invalid");
        }

        byte[] responseBytes= ByteBufUtil.getBytes(msg.content());

        if (responseBytes.length==0) {
            throw new RpcException("response data empty");
        }

        RpcResponse response= (RpcResponse) serializer.deserializer(responseBytes,RpcResponse.class);

        rpcInvokerFactory.notifyInvokerFuture(response.getRequestId(),response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(">>>>>>>>>netty_http client caught exception",cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            nettyHttpConnectClient.send(Beat.BEAT_PING);
            LOGGER.debug(">>>>>>>>>>> xxl-rpc netty_http client send beat-ping.");
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }
}
