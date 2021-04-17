package com.cxl.rpc.proxy.consumer.route;

import java.util.TreeSet;

/**
 * @author cxl
 */
public abstract class RpcLoadBalance {
    public abstract String route(String serviceKey, TreeSet<String> addressSet);
}
