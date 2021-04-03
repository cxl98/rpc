package com.cxl.rpc.proxy.consumer.callback.callbackStrategy;

import com.cxl.rpc.proxy.consumer.callback.CallBack;
import com.cxl.rpc.proxy.consumer.callback.CallType;
import com.cxl.rpc.proxy.consumer.callback.RpcInvokeCallback;
import com.cxl.rpc.proxy.net.params.RpcRequest;
import com.cxl.rpc.util.RpcException;

/**
 * @author cxl
 */
public class CallBackStrategy extends CallBack {

    @Override
    public Object export(RpcRequest request) {
        RpcInvokeCallback callback=RpcInvokeCallback.getCallback();
        if (null==callback){
            throw new RpcException("rpc RpcInvokeCallback（CallType="+ CallType.CALLBACK.name() +"） cannot be null.");
        }
        rpcFutureResponse.addInvokeCallback(callback);
        try {
            client.asyncSend(address,request);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            rpcInvokerFactory.removeInvokerFuture(request.getRequestId());
        }
        return null;
    }
}
