package com.cxl.rpc.proxy.net.impl.netty_http.server;

import com.cxl.rpc.proxy.net.Server;
import com.cxl.rpc.proxy.net.params.Beat;
import com.cxl.rpc.proxy.provider.RpcProviderFactory;
import com.cxl.rpc.util.ThreadPoolUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyHttpServer extends Server {

    @Override
    public void start(RpcProviderFactory rpcProviderFactory) {
        final ThreadPoolExecutor serverHandlerPool = ThreadPoolUtil.ThreadPool(NettyHttpServer.class.getSimpleName());
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new IdleStateHandler(0, 0, Beat.BEAT_INTERVAL * 3, TimeUnit.SECONDS)).addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(5 * 1024 * 1024)).addLast(new NettyHttpServerHandler(rpcProviderFactory, serverHandlerPool));
                        }
                    }).childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(rpcProviderFactory.getPort()).sync();

            LOGGER.info(">>>>>>>>>>> rpc remoting server start success, nettype = {}, port = {}", NettyHttpServer.class.getName(), rpcProviderFactory.getPort());

            onStarted();

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            if (e instanceof InterruptedException) {
                LOGGER.info(">>>>>>>>>>> xxl-rpc remoting server stop.");
            } else {
                LOGGER.error(">>>>>>>>>>> xxl-rpc remoting server error.", e);
            }
            //stop
            if (serverHandlerPool != null) {
                serverHandlerPool.shutdown();
            }
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    @Override
    public void stop() {
        onStop();
        LOGGER.info(">>>>>> remoting server destroy success");
    }
}
