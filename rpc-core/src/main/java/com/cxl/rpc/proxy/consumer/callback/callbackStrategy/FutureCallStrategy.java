package com.cxl.rpc.proxy.consumer.callback.callbackStrategy;

import com.cxl.rpc.proxy.consumer.callback.CallBack;
import com.cxl.rpc.proxy.consumer.callback.RpcInvokeFuture;
import com.cxl.rpc.proxy.net.params.RpcRequest;

public class FutureCallStrategy extends CallBack {


    @Override
    public Object export(RpcRequest request) {
        try {
            RpcInvokeFuture invokeFuture=new RpcInvokeFuture(rpcFutureResponse);
            invokeFuture.setFuture(invokeFuture);
            client.asyncSend(address, request);
        } catch (Exception e) {
            LOGGER.info(">>>>>rpc,invoke error,address:{},RpcRequest:{}",address,request);
            e.printStackTrace();
        }finally {
            if (null!=rpcInvokerFactory){
                rpcInvokerFactory.removeInvokerFuture(request.getRequestId());
            }
        }
        return null;
    }
}
