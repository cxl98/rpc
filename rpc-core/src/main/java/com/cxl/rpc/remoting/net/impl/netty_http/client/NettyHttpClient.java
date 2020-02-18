package com.cxl.rpc.remoting.net.impl.netty_http.client;

import com.cxl.rpc.remoting.net.Client;
import com.cxl.rpc.remoting.net.common.ConnectClient;
import com.cxl.rpc.remoting.net.params.RpcRequest;

public class NettyHttpClient extends Client {
    private Class<? extends ConnectClient> connectClientImpl=NettyHttpConnectClient.class;
    @Override
    public void asyncSend(String address, RpcRequest rpcRequest) throws Exception {
        ConnectClient.asyncSend(rpcRequest,address,connectClientImpl,rpcReferenceBean);
    }

}
