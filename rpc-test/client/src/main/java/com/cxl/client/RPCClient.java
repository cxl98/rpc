package com.cxl.client;

import com.cxl.api.Deom;
import com.cxl.api.dto.UserDTO;
import com.cxl.rpc.remoting.consumer.RpcInvokerFactory;
import com.cxl.rpc.remoting.consumer.call.CallType;
import com.cxl.rpc.remoting.consumer.call.RpcInvokeCallback;
import com.cxl.rpc.remoting.consumer.call.RpcInvokeFuture;
import com.cxl.rpc.remoting.consumer.reference.RpcReferenceBean;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RPCClient {
    public static void main(String[] args) throws Exception {
//        testCALLBACK();
//        testSYNC();
        testOneWay();
//        testFuture();


//        TimeUnit.SECONDS.sleep(2);


        RpcInvokerFactory.getInstance().stop();
    }

    private static void testSYNC() {
//        Deom deom= (Deom) new RpcReferenceBean(NetEnum.NETTY, Serializer.SerializerEnum.PROTOSTUFF.getSerializer(), CallType.SYNC, LoadBalance.ROUND,Deom.class,null,500,"127.0.0.1:8888",null,null,null).getObject();
//        Deom deom= (Deom) new RpcReferenceBean();
        RpcReferenceBean referenceBean = new RpcReferenceBean();
        referenceBean.setIface(Deom.class);
        referenceBean.setAddress("127.0.0.1:8888");
        Deom deom = (Deom) referenceBean.getObject();
        UserDTO say = (UserDTO) deom.say("123456", "123456");
        System.out.println(say);

    }

    private static void testCALLBACK() {
        RpcReferenceBean referenceBean = new RpcReferenceBean();
        referenceBean.setIface(Deom.class);
        referenceBean.setCallType(CallType.CALLBACK);
        referenceBean.setAddress("127.0.0.1:8888");
        Deom deom = (Deom) referenceBean.getObject();
        RpcInvokeCallback.setCallback(new RpcInvokeCallback<UserDTO>() {

            @Override
            public void onSuccess(UserDTO result) {
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
        Future<UserDTO> userDTOFuture = RpcInvokeFuture.getFuture();
        UserDTO userDTO = userDTOFuture.get();
        System.out.println(userDTO);
    }

    private static void testOneWay() {
        RpcReferenceBean referenceBean = new RpcReferenceBean();
        referenceBean.setIface(Deom.class);
        referenceBean.setCallType(CallType.ONEWAY);
        referenceBean.setAddress("127.0.0.1:8888");
        Deom deom = (Deom) referenceBean.getObject();
        UserDTO say = (UserDTO) deom.say("123456", "123456");
        System.out.println(say);
    }
}
