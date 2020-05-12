package com.cxl.rpc.util;

import java.util.concurrent.*;

public class ThreadPoolUtil {

    public static ThreadPoolExecutor makeServerThreadPool(final String serverType) {
        ThreadPoolExecutor serverHandlerPool=new ThreadPoolExecutor(
                60, 300, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000), r -> new Thread(r, "rpc" + serverType + "-serverHandlerPool-" + r.hashCode()), (r, executor) -> {
                    throw new RuntimeException("rpc"+serverType+"Thread pool is EXHAUSTED!");
                });// default maxThreads 300, minThreads 60
        return serverHandlerPool;
    }
}
