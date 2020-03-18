package com.cxl.rpc.remoting.invoker.call;

import com.cxl.rpc.remoting.net.params.RpcFutureResponse;
import com.cxl.rpc.remoting.net.params.RpcResponse;
import com.cxl.rpc.util.RpcException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RpcInvokeFuture implements Future {
    private RpcFutureResponse futureResponse;

    public RpcInvokeFuture(RpcFutureResponse futureResponse) {
        this.futureResponse = futureResponse;
    }

    public void stop(){
        //remove-InvokerFuture
        futureResponse.removeInvokerFuture();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return futureResponse.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return futureResponse.isCancelled();
    }

    @Override
    public boolean isDone() {
        return futureResponse.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        try {
            return get(-1,TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            //future get
            RpcResponse response=futureResponse.get(timeout,unit);

            if (response.getErrorMsg() != null) {
                throw new RpcException(response.getErrorMsg());
            }
            return response.getResult();
        } finally {
            stop();
        }
    }

    private static ThreadLocal<RpcInvokeFuture> threadInvokerFuture=new ThreadLocal<>();

    /**
     * get future
     * @param <T>
     * @return
     */
    public static <T> Future<T> getFuture(){
        Future<T> future=(Future<T>) threadInvokerFuture.get();
        threadInvokerFuture.remove();
        return future;
    }

    public static void setFuture(RpcInvokeFuture future){
        threadInvokerFuture.set(future);
    }

    public static void removeFuture(){
        threadInvokerFuture.remove();
    }
}
