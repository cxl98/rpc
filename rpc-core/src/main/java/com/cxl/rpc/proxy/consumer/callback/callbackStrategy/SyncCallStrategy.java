package com.cxl.rpc.proxy.consumer.callback.callbackStrategy;

import com.cxl.rpc.proxy.consumer.callback.CallBack;
import com.cxl.rpc.proxy.net.params.RpcRequest;
import com.cxl.rpc.proxy.net.params.RpcResponse;
import com.cxl.rpc.util.RpcException;

import java.util.concurrent.TimeUnit;

/**
 * @author cxl
 */
public class SyncCallStrategy extends CallBack {


    @Override
    public Object export(RpcRequest request) {
        RpcResponse response;
        try {
            client.asyncSend(address, request);
            response = rpcFutureResponse.get(5000, TimeUnit.MILLISECONDS);
            if (null != response.getErrorMsg()) {
                throw new RpcException(response.getErrorMsg());
            }
            return response.getResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rpcInvokerFactory.removeInvokerFuture(request.getRequestId());
        }
        return null;
    }
}
