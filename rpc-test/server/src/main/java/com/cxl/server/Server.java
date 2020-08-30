package com.cxl.server;

import com.cxl.api.Deom;
import com.cxl.rpc.remoting.provider.RpcProviderFactory;
import com.cxl.server.impl.DemoImpl;

public class Server {
    public static void main(String[] args) throws Exception {
        RpcProviderFactory factory=new RpcProviderFactory();
//        factory.setServiceRegistryClass(LocalRegistry.class);
//        factory.initConfig(NetEnum.NETTY, Serializer.SerializerEnum.JACKSON.getSerializer(),-1,-1,null,8008,null,null,null);

        factory.addService(Deom.class.getName(),null,new DemoImpl());


        factory.start();

    }
}
