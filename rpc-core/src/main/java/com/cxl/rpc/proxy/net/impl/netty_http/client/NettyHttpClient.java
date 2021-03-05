package com.cxl.rpc.proxy.net.impl.netty_http.client;

import com.cxl.rpc.proxy.net.Client;
import com.cxl.rpc.proxy.net.ConnectClient;
import com.cxl.rpc.proxy.net.params.RpcRequest;

public class NettyHttpClient extends Client {
    private Class<? extends ConnectClient> connectClientImpl=NettyHttpConnectClient.class;
    @Override
    public void asyncSend(String address, RpcRequest rpcRequest) throws Exception {
        ConnectClient.asyncSend(rpcRequest,address,connectClientImpl,rpcReferenceBean);
    }

}
