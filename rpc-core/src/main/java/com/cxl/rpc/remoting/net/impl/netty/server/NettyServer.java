package com.cxl.rpc.remoting.net.impl.netty.server;

import com.cxl.rpc.remoting.net.Server;
import com.cxl.rpc.remoting.net.impl.netty.codec.NettyDecoder;
import com.cxl.rpc.remoting.net.impl.netty.codec.NettyEncoder;
import com.cxl.rpc.remoting.net.params.Beat;
import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.remoting.net.params.RpcResponse;
import com.cxl.rpc.remoting.provider.RpcProviderFactory;
import com.cxl.rpc.util.ThreadPoolUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyServer extends Server {

    private Thread thread;

    @Override
    public void start(final RpcProviderFactory rpcProviderFactory) throws Exception {
        thread = new Thread(() -> {
            //param
            final ThreadPoolExecutor serverHandlerPool = ThreadPoolUtil.ThreadPool(NettyServer.class.getSimpleName());
            EventLoopGroup bossGroup = new EpollEventLoopGroup();
            EventLoopGroup workGroup = new EpollEventLoopGroup();

            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workGroup).channel(EpollServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new IdleStateHandler(0, 0, Beat.BEAT_INTERVAL * 3, TimeUnit.SECONDS))
                                .addLast(new NettyDecoder(RpcRequest.class, rpcProviderFactory.getSerializer()))
                                .addLast(new NettyEncoder(RpcResponse.class, rpcProviderFactory.getSerializer()))
                                .addLast(new NettyServerHandler(rpcProviderFactory, serverHandlerPool));
                    }
                }).childOption(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                //bind

                ChannelFuture future = bootstrap.bind(rpcProviderFactory.getPort()).sync();

                LOGGER.info(">>>>>>>>>>>>rpc remoting server start success , netType = {} , port ={}",NettyServer.class.getName(),rpcProviderFactory.getPort());
                onStarted();

                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                LOGGER.info(">>>>>>>>>>>>>>>>>rpc remoting server stop");
            }finally {
                try {
                    serverHandlerPool.shutdown();
                    workGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(),e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() throws Exception {
        //destroy server thread
        if (thread != null&& thread.isAlive()) {
            thread.interrupt();
        }

        //on stop
        onStop();
        LOGGER.info(">>>>>>>>>>> xxl-rpc remoting server destroy success.");
    }
}
