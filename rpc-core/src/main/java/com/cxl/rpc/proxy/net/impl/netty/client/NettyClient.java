package com.cxl.rpc.proxy.net.impl.netty.client;

import com.cxl.rpc.proxy.net.Client;
import com.cxl.rpc.proxy.net.ConnectClient;
import com.cxl.rpc.proxy.net.params.RpcRequest;

public class NettyClient extends Client {
    private final Class<? extends ConnectClient> connectClientImpl=NettyConnerClient.class;
    @Override
    public void asyncSend(String address, RpcRequest rpcRequest) throws Exception {
        ConnectClient.asyncSend(rpcRequest,address,connectClientImpl,rpcReferenceBean);
    }



}
