package com.cxl.rpc.remoting.consumer.call;

public abstract class RpcInvokeCallback<T> {
    public abstract void onSuccess(T result);

    public abstract void onFailure(Throwable exception);

    //===================thread invoke callback======================
    //callback , can be null
    private static ThreadLocal<RpcInvokeCallback> threadInvokerFuture=new ThreadLocal<>();

    /**
     * get callback
     */
    public static RpcInvokeCallback getCallback(){
        RpcInvokeCallback invokeCallback=threadInvokerFuture.get();
        return invokeCallback;
    }

    /**
     * set future
     */
    public static void setCallback(RpcInvokeCallback invokeCallback){
        threadInvokerFuture.set(invokeCallback);
    }

    /**
     * remove future
     */

    private static void removeCallback(){
        threadInvokerFuture.remove();
    }
}
