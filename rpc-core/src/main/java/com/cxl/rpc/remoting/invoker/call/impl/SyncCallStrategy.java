package com.cxl.rpc.remoting.invoker.call.impl;

import com.cxl.rpc.remoting.invoker.RpcInvokerFactory;
import com.cxl.rpc.remoting.invoker.call.CallBack;
import com.cxl.rpc.remoting.invoker.reference.RpcReferenceBean;
import com.cxl.rpc.remoting.net.Client;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.remoting.net.params.RpcFutureResponse;
import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.remoting.net.params.RpcResponse;
import com.cxl.rpc.util.RpcException;

import java.util.concurrent.TimeUnit;

public class SyncCallStrategy extends CallBack {


    @Override
    public RpcResponse export(NetEnum type, String address, RpcRequest request, RpcReferenceBean rpcReferenceBean) {
        RpcInvokerFactory invokerFactory = rpcReferenceBean.getInvokerFactory();
        rpcFutureResponse=new RpcFutureResponse(invokerFactory,request);
        RpcResponse response;
        try {
            Client client = rpcReferenceBean.getClient();
            client.asyncSend(address,request);
             response=rpcFutureResponse.get(rpcReferenceBean.getTimeout(), TimeUnit.MILLISECONDS);
            if (null!=response.getErrorMsg()){
                throw new RpcException(response.getErrorMsg());
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
