package com.cxl.rpc.remoting.net.impl.netty.client;

import com.cxl.rpc.remoting.net.Client;
import com.cxl.rpc.remoting.net.ConnectClient;
import com.cxl.rpc.remoting.net.params.RpcRequest;

public class NettyClient extends Client {
    private Class<? extends ConnectClient> connectClientImpl=NettyConnerClient.class;
    @Override
    public void asyncSend(String address, RpcRequest rpcRequest) throws Exception {
        ConnectClient.asyncSend(rpcRequest,address,connectClientImpl,rpcReferenceBean);
    }
}
