package com.cxl.rpc.remoting.net;

import com.cxl.rpc.remoting.invoker.reference.RpcReferenceBean;
import com.cxl.rpc.remoting.net.params.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Client {
    private static final Logger LOGGER= LoggerFactory.getLogger(Client.class);

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
