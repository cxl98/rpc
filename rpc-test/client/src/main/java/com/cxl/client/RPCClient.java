package com.cxl.client;

import com.cxl.api.Deom;
import com.cxl.api.dto.UserDTO;
import com.cxl.rpc.remoting.invoker.RpcInvokerFactory;
import com.cxl.rpc.remoting.invoker.call.CallType;
import com.cxl.rpc.remoting.invoker.reference.RpcReferenceBean;
import com.cxl.rpc.remoting.invoker.route.LoadBalance;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.serialize.Serializer;

import java.util.concurrent.TimeUnit;

public class RPCClient {
    public static void main(String[] args) throws Exception {
        testSYNC();
        TimeUnit.SECONDS.sleep(2);

//        RpcInvokerFactory.getInstance().stop();
    }

    private static void testSYNC() {
        Deom deom= (Deom) new RpcReferenceBean(NetEnum.NETTY, Serializer.SerializerEnum.PROTOSTUFF.getSerializer(), CallType.SYNC, LoadBalance.ROUND,Deom.class,null,500,"127.0.0.1:8888",null,null,null).getObject();

        UserDTO user= (UserDTO) deom.say("123456","123456");
        System.out.println(user);

    }
}
