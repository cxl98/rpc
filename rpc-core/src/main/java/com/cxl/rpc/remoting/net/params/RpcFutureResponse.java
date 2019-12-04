package com.cxl.rpc.remoting.net.params;

import com.cxl.rpc.remoting.invoker.RpcInvokerFactory;
import com.cxl.rpc.remoting.invoker.call.RpcInvokeCallback;
import com.cxl.rpc.util.RpcException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RpcFutureResponse implements Future<RpcResponse> {
    private RpcInvokerFactory invokerFactory;

    //net data
    private RpcRequest request;
    private RpcResponse response;

    //future lock
    private boolean done=false;
    private Object lock=new Object();

    //callback , can be null
    private RpcInvokeCallback invokeCallback;

    public RpcFutureResponse(RpcInvokerFactory invokerFactory, RpcInvokeCallback invokeCallback) {
        this.invokerFactory = invokerFactory;
        this.invokeCallback = invokeCallback;

        //set-InvokerFuture
        setInvokerFuture();
    }

    //-------------------------response pool-------------------------------

    private void setInvokerFuture() {
        this.invokerFactory.setInvokerFuture(request.getRequestId(),this);
    }
    public void removeInvokerFuture(){
        this.invokerFactory.removeInvokerFuture(request.getRequestId());
    }



    //---------------------get-------------------
    public RpcRequest getRequest(){
        return request;
    }
    public RpcInvokeCallback getInvokeCallback(){
        return invokeCallback;
    }

    //-------------------for invoke back-------------------------

    public void setResponse(RpcResponse response){
        this.response=response;
        synchronized (lock){
            done=true;
            lock.notifyAll();
        }
    }

    //------------------------for invoke-------------------------
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        try {
            return get(-1,TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!done) {
            synchronized (lock){
                try {
                    if (timeout<0){
                        lock.wait();
                    }else{
                        long timeoutMillis=(TimeUnit.MILLISECONDS==unit)?timeout:TimeUnit.MILLISECONDS.convert(timeout,unit);
                        lock.wait(timeoutMillis);
                    }
                } catch (InterruptedException e) {
                    throw e;
                }
            }
        }
        if (!done) {
            throw new RpcException("rpc, request timeout at:"+System.currentTimeMillis()+", request:"+request.toString());
        }
        return response;
    }
}
