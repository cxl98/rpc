package com.cxl.rpc.remoting.invoker.call;

public abstract class RpcInvokeCallback<T> {
    public abstract void onSuccess(T result);

    public abstract void onFailure(Throwable exception);

    //===================thread invoke callback======================

}
