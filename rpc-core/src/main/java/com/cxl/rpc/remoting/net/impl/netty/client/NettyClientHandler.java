package com.cxl.rpc.remoting.net.impl.netty.client;

import com.cxl.rpc.remoting.invoker.RpcInvokerFactory;
import com.cxl.rpc.remoting.net.params.Beat;
import com.cxl.rpc.remoting.net.params.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER= LoggerFactory.getLogger(NettyClientHandler.class);

    private RpcInvokerFactory invokerFactory;
    private NettyConnerClient nettyConnerClient;

    public NettyClientHandler(final RpcInvokerFactory invokerFactory, NettyConnerClient nettyConnerClient) {
        this.invokerFactory = invokerFactory;
        this.nettyConnerClient = nettyConnerClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        invokerFactory.notifyInvokerFuture(msg.getRequestId(),msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(">>>>>>>>>>>rpc netty client caught exception",cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            nettyConnerClient.send(Beat.BEAT_PING);

            LOGGER.debug(">>>>>>>>>>>>>>>>>rpc netty client send beat-ping");
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }
}
