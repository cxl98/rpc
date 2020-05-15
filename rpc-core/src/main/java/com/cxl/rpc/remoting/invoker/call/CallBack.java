package com.cxl.rpc.remoting.invoker.call;

import com.cxl.rpc.remoting.invoker.reference.RpcReferenceBean;
import com.cxl.rpc.remoting.net.Client;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.remoting.net.params.RpcFutureResponse;
import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.remoting.net.params.RpcResponse;

public abstract class CallBack {
   protected RpcFutureResponse rpcFutureResponse;
   public abstract RpcResponse export(NetEnum type,String address, RpcRequest request,RpcReferenceBean rpcReferenceBean);
}
