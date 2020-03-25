package com.cxl.clent;

import com.cxl.api.Deom;
import com.cxl.api.dto.UserDTO;
import com.cxl.rpc.remoting.invoker.RpcInvokerFactory;
import com.cxl.rpc.remoting.invoker.call.CallType;
import com.cxl.rpc.remoting.invoker.reference.RpcReferenceBean;
import com.cxl.rpc.remoting.invoker.route.LoadBalance;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.serialize.Serializer;

import java.util.concurrent.TimeUnit;

public class Client {
    public static void main(String[] args) throws Exception {
        testSYNC();
        TimeUnit.SECONDS.sleep(2);

        RpcInvokerFactory.getInstance().stop();
    }

    private static void testSYNC() {
        Deom deom= (Deom) new RpcReferenceBean(NetEnum.NETTY, Serializer.SerializerEnum.PROTOSTUFF.getSerializer(), CallType.SYNC, LoadBalance.ROUND,Deom.class,null,500,"127.0.0.1:8008",null,null,null).getObject();

        UserDTO user=deom.say("[SYNC] 陈新林");
        System.out.println("xxxxxxxxxx");
        System.out.println(user);

    }
}
