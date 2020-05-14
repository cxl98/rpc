package com.cxl.rpc.remoting.invoker.call;

import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.remoting.net.params.RpcResponse;

public abstract class CallBack {

   public abstract RpcResponse export(String address, RpcRequest request);
}
