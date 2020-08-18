package com.cxl.rpc.remoting.consumer.call.impl;

import com.cxl.rpc.remoting.consumer.call.CallBack;
import com.cxl.rpc.remoting.consumer.call.RpcInvokeFuture;
import com.cxl.rpc.remoting.net.params.RpcRequest;

public class FutureCallStrategy extends CallBack {


    @Override
    public Object export(RpcRequest request) {
        try {
            RpcInvokeFuture invokeFuture=new RpcInvokeFuture(rpcFutureResponse);
            invokeFuture.setFuture(invokeFuture);
            client.asyncSend(address, request);
        } catch (Exception e) {
            LOGGER.info(">>>>>rpc,invoke error,address:{},RpcRequest:{}",address,request);
            rpcInvokerFactory.removeInvokerFuture(request.getRequestId());
            e.printStackTrace();
        }
        return null;
    }
}
