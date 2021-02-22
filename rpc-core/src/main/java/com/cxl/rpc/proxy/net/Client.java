package com.cxl.rpc.proxy.net;

import com.cxl.rpc.proxy.consumer.reference.RpcReferenceBean;
import com.cxl.rpc.proxy.net.params.RpcRequest;

public abstract class Client {

    //---------------------init--------------------------
    protected volatile RpcReferenceBean rpcReferenceBean;

    public void init(RpcReferenceBean rpcReferenceBean){
        this.rpcReferenceBean=rpcReferenceBean;
    }

    //-------------------------send----------------------------
    /**
     * async send, bind requestId and future-response
     *
     * @param address
     * @param rpcRequest
     * @return
     * @throws Exception
     */

    public abstract void asyncSend(String address, RpcRequest rpcRequest) throws Exception;
}
