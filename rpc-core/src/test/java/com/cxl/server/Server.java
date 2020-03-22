package com.cxl.server;

import com.cxl.api.Demo;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.remoting.provider.RpcProviderFactory;
import com.cxl.rpc.serialize.Serializer;

import java.util.concurrent.TimeUnit;

public class Server {
    public static void main(String[] args) throws Exception {
        RpcProviderFactory factory=new RpcProviderFactory();

        factory.initConfig(NetEnum.NETTY,Serializer.SerializerEnum.JACKSON.getSerializer() ,-1,-1,null,8000,null,null,null);

        factory.addService(Demo.class.getName(),null,new DemoImpl());
        factory.start();

        while(!Thread.currentThread().isInterrupted()){
            TimeUnit.HOURS.sleep(1);
        }

        factory.stop();
    }
}
