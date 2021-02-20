package com.cxl.rpc.proxy.consumer.callback;

import com.cxl.rpc.proxy.consumer.RpcInvokerFactory;
import com.cxl.rpc.proxy.consumer.reference.RpcReferenceBean;
import com.cxl.rpc.proxy.net.Client;
import com.cxl.rpc.proxy.net.params.RpcFutureResponse;
import com.cxl.rpc.proxy.net.params.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CallBack {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CallBack.class);
    protected RpcFutureResponse rpcFutureResponse;
    protected RpcInvokerFactory rpcInvokerFactory;
    protected Client client;
    protected String address;

    public Object export(RpcRequest request, RpcReferenceBean rpcReferenceBean) {
        rpcInvokerFactory = rpcReferenceBean.getInvokerFactory();
        rpcFutureResponse = new RpcFutureResponse(request);
        rpcInvokerFactory.setInvokerFuture(request.getRequestId(), rpcFutureResponse);
        client = rpcReferenceBean.getClient();
        address = rpcReferenceBean.getAddress();
        try {
            return export(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract Object export(RpcRequest request);
}
