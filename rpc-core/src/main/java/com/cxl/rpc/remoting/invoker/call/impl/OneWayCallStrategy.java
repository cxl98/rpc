package com.cxl.rpc.remoting.invoker.call.impl;

import com.cxl.rpc.remoting.invoker.call.CallBack;
import com.cxl.rpc.remoting.net.params.RpcRequest;

public class OneWayCallStrategy extends CallBack {



    @Override
    public Object export(RpcRequest request) {
        try {
            client.asyncSend(address,request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
