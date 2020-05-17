package com.cxl.rpc.remoting.invoker.call.impl;

import com.cxl.rpc.remoting.invoker.call.CallBack;
import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.remoting.net.params.RpcResponse;
import com.cxl.rpc.util.RpcException;

import java.util.concurrent.TimeUnit;

public class SyncCallStrategy extends CallBack {


    @Override
    public Object export(RpcRequest request) {
        RpcResponse response;
        try {
            client.asyncSend(address, request);
            response = rpcFutureResponse.get(500, TimeUnit.MILLISECONDS);
            if (null != response.getErrorMsg()) {
                throw new RpcException(response.getErrorMsg());
            }
            return response.getResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rpcFutureResponse.removeInvokerFuture();
        }
        return null;
    }
}
