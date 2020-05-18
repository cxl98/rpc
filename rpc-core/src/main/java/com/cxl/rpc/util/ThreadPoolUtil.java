package com.cxl.rpc.util;

import java.util.concurrent.*;

public class ThreadPoolUtil {

    public static ThreadPoolExecutor ThreadPool(final String name) {
        ThreadPoolExecutor executors=new ThreadPoolExecutor(
                60, 300, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000), r -> new Thread(r, "rpc" + name + "-Pool-" + r.hashCode()), (r, executor) -> {
                    throw new RuntimeException("rpc"+name+"Thread pool is EXHAUSTED!");
                });// default maxThreads 300, minThreads 60
        return executors;
    }
}
