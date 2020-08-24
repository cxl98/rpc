package com.cxl.rpc.util;

import com.cxl.rpc.remoting.net.params.RpcResponse;

public abstract class AbstractPush{
    public abstract void sendMsg(RpcResponse msg);
}
