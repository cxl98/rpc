package com.cxl.rpc.proxy.net.impl.netty_http.client;

import com.cxl.rpc.proxy.consumer.RpcInvokerFactory;
import com.cxl.rpc.proxy.net.ConnectClient;
import com.cxl.rpc.proxy.net.params.Beat;
import com.cxl.rpc.proxy.net.params.RpcRequest;
import com.cxl.rpc.serialize.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @author cxl
 */
public class NettyHttpConnectClient extends ConnectClient {
    private EventLoopGroup group;
    private Channel channel;
    private Serializer serializer;
    private String address;
    private String host;
    @Override
    public void init(String address, Serializer serializer, RpcInvokerFactory rpcInvokerFactory) throws Exception {
        final NettyHttpConnectClient connectClient=this;
        if (!address.toLowerCase().startsWith("http")) {
            address="https://"+address;
        }
        this.address=address;
        URL url=new URL(address);
        System.out.println("url"+url);
        this.host=url.getHost();
        System.out.println("host"+host);
        int port =url.getPort()>-1?url.getPort():80;
        System.out.println("port"+port);
        this.group=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(0,0, Beat.BEAT_INTERVAL, TimeUnit.SECONDS))
                                .addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(5*1024*1024))
                                .addLast(new NettyHttpClientHandler(rpcInvokerFactory,serializer,connectClient));
                    }
                }).option(ChannelOption.SO_KEEPALIVE,true);
        this.channel=bootstrap.connect(host,port).sync().channel();
        this.serializer=serializer;

        if (!isValidate()) {
            close();
            return;
        }
        logger.debug(">>>>>>>>> netty client proxy,connect to server success at host:{},port:{}",host,port);
    }

    @Override
    public void close() {
        if (this.channel != null&&this.channel.isActive()) {
            this.channel.close();
        }
        if (this.group != null&&this.group.isShutdown()) {
            this.group.shutdownGracefully();
        }
        logger.debug(">>>>>>>>>>.netty client close");
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
        byte[] requestBytes=serializer.serializer(rpcRequest);

        DefaultFullHttpRequest request=new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,new URI(address).getRawPath(), Unpooled.wrappedBuffer(requestBytes));
        request.headers().set(HttpHeaderNames.HOST,host);
        request.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
        this.channel.writeAndFlush(request).sync();
    }
}
