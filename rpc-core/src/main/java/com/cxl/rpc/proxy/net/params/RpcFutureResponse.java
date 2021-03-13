package com.cxl.rpc.proxy.net.params;

import com.cxl.rpc.proxy.consumer.RpcInvokerFactory;
import com.cxl.rpc.proxy.consumer.callback.RpcInvokeCallback;
import com.cxl.rpc.util.RpcException;
import com.cxl.rpc.util.ThreadPoolUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

public class RpcFutureResponse implements Future<RpcResponse> {
    //net data
    private RpcRequest request;
    private RpcResponse response;

    private Sync sync;
    private List<RpcInvokeCallback> invokeCallbacks = new ArrayList<>();
    private ThreadPoolExecutor threadPoolExecutor = null;

    private ReentrantLock locks = new ReentrantLock();


    public RpcFutureResponse(RpcRequest request) {
        this.request = request;
        this.sync = new Sync();
    }

    //-------------------for invoke back(回调)-------------------------

    public void setResponse(RpcResponse response) {
        this.response = response;
        sync.release(1);
    }

    public void call(RpcResponse rpcResponse) {
        this.response = rpcResponse;
        sync.release(1);
        invokeCallbacks();

    }

    private void invokeCallbacks() {
        locks.lock();
        try {
            for (final RpcInvokeCallback item : invokeCallbacks) {
                run(item);
            }
        } finally {
            locks.unlock();
        }
    }

    public void addInvokeCallback(RpcInvokeCallback invokeCallback) {
        locks.lock();
        try {
            if (isDone()) {
                run(invokeCallback);
            } else {
                if (null != invokeCallbacks && invokeCallbacks.isEmpty()) {
                    this.invokeCallbacks.add(invokeCallback);
                } else {
                    throw new RpcException(">>>>>>invokeCallbacks is null");
                }
            }
        } finally {
            locks.unlock();
        }
    }

    public RpcInvokeCallback getInvokeCallback() {
        if (null != invokeCallbacks) {
            for (final RpcInvokeCallback item : invokeCallbacks) {
                return item;
            }
        }
        return null;
    }


    private void run(RpcInvokeCallback invokeCallback) {
        final RpcResponse res = this.response;
        try {
            execute(() -> {
                if (null != res.getErrorMsg()) {
                    invokeCallback.onFailure(new RpcException(res.getErrorMsg()));
                } else {
                    invokeCallback.onSuccess(res.getResult());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stopCallbackThreadPool();
        }
    }

    private void execute(Runnable runnable) {
        if (null == threadPoolExecutor) {
            synchronized (this) {
                if (null == threadPoolExecutor) {
                    threadPoolExecutor = ThreadPoolUtil.ThreadPool(RpcInvokerFactory.class.getName());

                }
            }
        }
        threadPoolExecutor.submit(runnable);
    }

    private void stopCallbackThreadPool() {
        if (null != threadPoolExecutor) {
            threadPoolExecutor.shutdown();
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
        return sync.isDone();
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        try {
            return get(5000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean b = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (b) {
            if (null != this.response) {
                return response;
            } else {
                return null;
            }
        } else {
            throw new RpcException("Timeout exception. Request :" + this.request);
        }
    }

    static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 111L;

        private static final int DONE = 1;
        private static final int PENDING = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == DONE;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (PENDING == getState()) {
                if (compareAndSetState(PENDING, DONE)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        public boolean isDone() {
            getState();
            return getState() == DONE;
        }
    }
}
