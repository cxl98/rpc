package com.cxl.rpc.remoting.invoker.route;

import java.util.TreeSet;

public abstract class RpcLoadBalance {
    public abstract String route(String serviceKey, TreeSet<String> addressSet);
}
