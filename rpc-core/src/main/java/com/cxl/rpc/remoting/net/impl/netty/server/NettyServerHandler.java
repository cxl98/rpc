package com.cxl.rpc.remoting.net.impl.netty.server;

import com.cxl.rpc.remoting.net.params.Beat;
import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.remoting.net.params.RpcResponse;
import com.cxl.rpc.remoting.provider.RpcProviderFactory;
import com.cxl.rpc.util.ThrowableUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger LOGGER= LoggerFactory.getLogger(NettyServerHandler.class);

    private RpcProviderFactory rpcProviderFactory;
    private ThreadPoolExecutor serverHandlerPool;

    public NettyServerHandler(final RpcProviderFactory rpcProviderFactory, final ThreadPoolExecutor serverHandlerPool) {
        this.rpcProviderFactory=rpcProviderFactory;
        this.serverHandlerPool=serverHandlerPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) {
        //filter beat
        if (Beat.BEAT_ID.equals(msg.getRequestId())) {
            LOGGER.debug(">>>>>>>>>>>>>>>>>>rpc provider netty server read beat-ping");
            return;
        }
        //do invoke
        try {
            serverHandlerPool.execute(() -> {
                //invoke +response
                RpcResponse response=rpcProviderFactory.invokeService(msg);
                ctx.writeAndFlush(response);
            });
        } catch (Exception e) {
            //catch error
            RpcResponse response=new RpcResponse();
            response.setRequestId(msg.getRequestId());
            response.setErrorMsg(ThrowableUtil.toString(e));
            ctx.writeAndFlush(response);

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(">>>>>>>>>>>>> rpc provider netty server caught exception.",cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            ctx.channel().close();
            LOGGER.debug(">>>>>>>>>>>>>>>>>>>>rpc provider netty server close an old channel.");
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }
}
