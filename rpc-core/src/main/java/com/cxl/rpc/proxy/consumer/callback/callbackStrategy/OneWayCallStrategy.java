package com.cxl.rpc.proxy.consumer.callback.callbackStrategy;

import com.cxl.rpc.proxy.consumer.callback.CallBack;
import com.cxl.rpc.proxy.net.params.RpcRequest;

/**
 * @author cxl
 */
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
