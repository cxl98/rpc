package com.cxl.rpc.proxy.net.impl.netty.client;

import com.cxl.rpc.proxy.consumer.RpcInvokerFactory;
import com.cxl.rpc.proxy.net.ConnectClient;
import com.cxl.rpc.proxy.net.impl.netty.codec.NettyDecoder;
import com.cxl.rpc.proxy.net.impl.netty.codec.NettyEncoder;
import com.cxl.rpc.proxy.net.params.Beat;
import com.cxl.rpc.proxy.net.params.RpcRequest;
import com.cxl.rpc.proxy.net.params.RpcResponse;
import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.IpUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class NettyConnerClient extends ConnectClient {

    private EventLoopGroup group;
    private Channel channel;

    @Override
    public void init(String address, final Serializer serializer, final RpcInvokerFactory rpcInvokerFactory) throws Exception {


        this.group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(0, 0, Beat.BEAT_INTERVAL, TimeUnit.SECONDS))  // beat N, close if fail
                                .addLast(new NettyEncoder(RpcRequest.class, serializer))
                                .addLast(new NettyDecoder(RpcResponse.class, serializer))
                                .addLast(new NettyClientHandler(rpcInvokerFactory));
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

        Object[] array = IpUtil.parseIpPort(address);
        String host = (String) array[0];
        int port = (int) array[1];
        this.channel = bootstrap.connect(host, port).sync().channel();

        if (!isValidate()) {
            close();
            return;
        }
        logger.debug(">>>>>>>>>>>>>>>>rpc netty client proxy, connect to server success at host:{}, port:{}", host, port);
    }

    @Override
    public void close() {
        if (this.channel != null && this.channel.isActive()) {
            this.channel.close();
        }
        if (this.group != null && !this.group.isShutdown()) {
            this.group.shutdownGracefully();
        }
        logger.debug("rpc netty client close");
    }

    @Override
    public boolean isValidate() {
        if (this.channel != null) {
            return this.channel.isActive();
        }
        return false;
    }

    @Override
    public void send(RpcRequest rpcRequest) throws Exception {
        this.channel.writeAndFlush(rpcRequest).sync();
    }
}
