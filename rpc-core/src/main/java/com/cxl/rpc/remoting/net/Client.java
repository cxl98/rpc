package com.cxl.rpc.remoting.net;

import com.cxl.rpc.remoting.consumer.reference.RpcReferenceBean;
import com.cxl.rpc.remoting.net.params.RpcRequest;

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
