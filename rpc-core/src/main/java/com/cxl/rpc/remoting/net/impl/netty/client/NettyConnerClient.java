package com.cxl.rpc.remoting.net.impl.netty.client;

import com.cxl.rpc.remoting.invoker.RpcInvokerFactory;
import com.cxl.rpc.remoting.net.common.ConnectClient;
import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.serialize.Serializer;

public class NettyConnerClient extends ConnectClient {
    @Override
    public void init(String address, Serializer serializer, RpcInvokerFactory rpcInvokerFactory) throws Exception {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isValidate() {
        return false;
    }

    @Override
    public void send(RpcRequest rpcRequest) throws Exception {

    }
}
