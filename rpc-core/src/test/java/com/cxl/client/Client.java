package com.cxl.client;

import com.cxl.api.Demo;
import com.cxl.api.User;
import com.cxl.rpc.remoting.invoker.RpcInvokerFactory;
import com.cxl.rpc.remoting.invoker.call.CallType;
import com.cxl.rpc.remoting.invoker.call.RpcInvokeCallback;
import com.cxl.rpc.remoting.invoker.call.RpcInvokeFuture;
import com.cxl.rpc.remoting.invoker.reference.RpcReferenceBean;
import com.cxl.rpc.remoting.invoker.route.LoadBalance;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.serialize.Serializer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Client {
    public static void main(String[] args) throws Exception {
        testSYNC();
//        testFUTURE();
//        testCCALLBACK();
//        testONEWAY();

        TimeUnit.SECONDS.sleep(1);
        RpcInvokerFactory.getInstance().stop();
    }

    private static void testSYNC() {
        Demo demo = (Demo) new RpcReferenceBean(NetEnum.NETTY, Serializer.SerializerEnum.JACKSON.getSerializer(), CallType.SYNC, LoadBalance.ROUND, Demo.class, null, 500, "127.0.0.1:8000", null, null, null).getObject();

        User user = demo.say("[SYNC] 陈新林");
        System.out.println(user);
    }

    private static void testFUTURE() throws ExecutionException, InterruptedException {
        Demo demo = (Demo) new RpcReferenceBean(NetEnum.NETTY, Serializer.SerializerEnum.JACKSON.getSerializer(), CallType.SYNC, LoadBalance.ROUND, Demo.class, null, 600, "127.0.0.1:8888", null, null, null).getObject();
        demo.say("[FUTURE] 陈新林");

        Future<User> userFuture= RpcInvokeFuture.getFuture();

        User user=userFuture.get();
        System.out.println(user.toString());
    }

    private static void testCCALLBACK() {
        Demo demo = (Demo) new RpcReferenceBean(NetEnum.NETTY, Serializer.SerializerEnum.JACKSON.getSerializer(), CallType.SYNC, LoadBalance.ROUND, Demo.class, null, 600, "127.0.0.1:8888", null, null, null).getObject();

        RpcInvokeCallback.setCallback(new RpcInvokeCallback() {
            @Override
            public void onSuccess(Object result) {
                System.out.println(result);
            }

            @Override
            public void onFailure(Throwable exception) {
                    exception.printStackTrace();
            }
        });

        demo.say("[CALLBACK]  陈新林");
    }

    private static void testONEWAY() {
        Demo demo = (Demo) new RpcReferenceBean(NetEnum.NETTY, Serializer.SerializerEnum.JACKSON.getSerializer(), CallType.SYNC, LoadBalance.ROUND, Demo.class, null, 600, "127.0.0.1:8888", null, null, null).getObject();

        demo.say("[ONEWAY] 陈新林");
    }
}
