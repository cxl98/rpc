package com.cxl.rpc.remoting.consumer.route;

import java.util.TreeSet;

public abstract class RpcLoadBalance {
    public abstract String route(String serviceKey, TreeSet<String> addressSet);
}
