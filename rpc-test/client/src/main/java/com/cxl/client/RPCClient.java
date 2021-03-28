package com.cxl.client;

import com.cxl.api.Deom;
import com.cxl.api.dto.UserDTO;
import com.cxl.rpc.proxy.consumer.callback.CallType;
import com.cxl.rpc.proxy.consumer.callback.RpcInvokeCallback;
import com.cxl.rpc.proxy.consumer.callback.RpcInvokeFuture;
import com.cxl.rpc.proxy.consumer.reference.RpcReferenceBean;
import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.serialize.impl.JacksonSerializer;
import com.cxl.rpc.serialize.impl.ProtostuffSerializer;
import com.cxl.rpc.util.ProxyPush;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RPCClient {
    static RpcReferenceBean referenceBean = new RpcReferenceBean();

    public static void main(String[] args) throws Exception {
//        testCALLBACK();
        testSYNC();
//        testOneWay();
//        testFuture();
//        TimeUnit.SECONDS.sleep(2);

//        RpcInvokerFactory.getInstance().stop();
    }

    private static void testSYNC() throws InterruptedException {
        referenceBean.setIface(Deom.class);
        referenceBean.setAddress("127.0.0.1:8888");
        referenceBean.setSerializer(JacksonSerializer.class);
//        referenceBean.setClientClass(NettyHttpClient.class);
        referenceBean.init();
        Deom deom = (Deom) referenceBean.getObject();
        System.out.println();
        Object say = deom.say("§¥%§+(", "123456");
//            ProxyPush.getInstance().setClassName(new PushImpl());
        System.out.println(say);
    }

    private static void testCALLBACK() throws InterruptedException {
        RpcReferenceBean referenceBean = new RpcReferenceBean();
        referenceBean.setIface(Deom.class);
        referenceBean.setCallType(CallType.CALLBACK);
        referenceBean.setAddress("127.0.0.1:8888");
        referenceBean.init();
        Deom deom = (Deom) referenceBean.getObject();
        RpcInvokeCallback.setCallback(new RpcInvokeCallback<Object>() {

            @Override
            public void onSuccess(Object result) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>xxxxxxx");
                System.out.println(result);
            }

            @Override
            public void onFailure(Throwable exception) {
                exception.printStackTrace();
            }
        });

        deom.say("123456", "123456");
    }

    private static void testFuture() throws ExecutionException, InterruptedException {
        RpcReferenceBean referenceBean = new RpcReferenceBean();
        referenceBean.setIface(Deom.class);
        referenceBean.setCallType(CallType.FUTURE);
        referenceBean.setAddress("127.0.0.1:8888");
        Deom deom = (Deom) referenceBean.getObject();
        deom.say("123456", "123456");
        Future<Object> userDTOFuture = RpcInvokeFuture.getFuture();
        Object userDTO = userDTOFuture.get();
        System.out.println(userDTO);
    }

    private static void testOneWay() throws InterruptedException {
        RpcReferenceBean referenceBean = new RpcReferenceBean();
        referenceBean.setIface(Deom.class);
        referenceBean.setCallType(CallType.ONEWAY);
        referenceBean.setAddress("127.0.0.1:8888");
        Deom deom = (Deom) referenceBean.getObject();
        Object say =  deom.say("123456", "123456");
        System.out.println(say);
    }
}
