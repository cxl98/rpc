package com.cxl.rpc.remoting.invoker.call.impl;

import com.cxl.rpc.remoting.invoker.call.CallBack;
import com.cxl.rpc.remoting.invoker.call.CallType;
import com.cxl.rpc.remoting.invoker.call.RpcInvokeCallback;
import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.util.RpcException;

public class CallBackStrategy extends CallBack {

    @Override
    public Object export(RpcRequest request) {
        RpcInvokeCallback callback=RpcInvokeCallback.getCallback();
        if (null==callback){
            throw new RpcException("rpc RpcInvokeCallback（CallType="+ CallType.CALLBACK.name() +"） cannot be null.");
        }
        rpcFutureResponse.setInvokeCallback(callback);
        try {
            client.asyncSend(address,request);
        } catch (Exception e) {
            rpcFutureResponse.removeInvokerFuture();
            e.printStackTrace();
        }
        return null;
    }
}
