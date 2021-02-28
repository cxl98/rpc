package com.cxl.rpc.proxy.net.impl.netty.client;

import com.cxl.rpc.proxy.consumer.RpcInvokerFactory;
import com.cxl.rpc.proxy.net.params.Beat;
import com.cxl.rpc.proxy.net.params.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER= LoggerFactory.getLogger(NettyClientHandler.class);

    private RpcInvokerFactory invokerFactory;

    public NettyClientHandler(final RpcInvokerFactory invokerFactory) {
        this.invokerFactory = invokerFactory;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) {
        invokerFactory.notifyInvokerFuture(msg.getRequestId(),msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error(">>>>>>>>>>>rpc netty client caught exception",cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            ctx.writeAndFlush(Beat.BEAT_PING);
            LOGGER.debug(">>>>>>>>>>>>>>>>>rpc netty client send beat-ping");
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }
}
