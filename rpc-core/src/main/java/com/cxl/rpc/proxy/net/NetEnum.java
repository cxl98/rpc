package com.cxl.rpc.proxy.net;

import com.cxl.rpc.proxy.net.impl.netty.client.NettyClient;
import com.cxl.rpc.proxy.net.impl.netty.server.NettyServer;
import com.cxl.rpc.proxy.net.impl.netty_http.client.NettyHttpClient;
import com.cxl.rpc.proxy.net.impl.netty_http.server.NettyHttpServer;

public enum  NetEnum {

    /**
     * netty tcp server
     */
    NETTY(NettyServer.class, NettyClient.class),
    /**
     * netty http server
     */
    NETTY_HTTP(NettyHttpServer.class, NettyHttpClient.class);
    public final Class<? extends Server> serverClass;
    public final Class<? extends Client> clientClass;

    NetEnum(Class<? extends Server> serverClass, Class<? extends Client> clientClass) {
        this.serverClass = serverClass;
        this.clientClass = clientClass;
    }

    public static NetEnum autoMatch(String name,NetEnum defaultEnum){
        for (NetEnum item: NetEnum.values()) {
            if (item.name().equals(name)){
                return item;
            }
        }
        return defaultEnum;
    }
}
