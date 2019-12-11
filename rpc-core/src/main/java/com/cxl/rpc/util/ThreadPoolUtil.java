package com.cxl.rpc.util;

import java.util.concurrent.*;

public class ThreadPoolUtil {

    public static ThreadPoolExecutor makeServerThreadPool(final String serverType) {
        ThreadPoolExecutor serverHandlerPool=new ThreadPoolExecutor(
                60, 300, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(1000), new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, "rpc" + serverType + "-serverHandlerPool-" + r.hashCode());
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                throw new RuntimeException("rpc"+serverType+"Thread pool is EXHAUSTED!");
            }
        });// default maxThreads 300, minThreads 60
        return serverHandlerPool;
    }
}
