package com.cxl.rpc.remoting.consumer.route;

import com.cxl.rpc.remoting.consumer.route.impl.*;

import java.util.TreeSet;

public enum  LoadBalance {
    RANDOM(new RpcLoadBalanceRandomStrategy()),
    ROUND(new RpcLoadBalanceRoundStrategy()),
    LRU(new RpcLoadBalanceLRUStrategy()),
    LFU(new RpcLoadBalanceLFUStrategy()),
    CONSISTENT_HASH(new RpcLoadBalanceConsistentHashStrategy());

    public final RpcLoadBalance rpcLoadBalance;

    LoadBalance(RpcLoadBalance rpcLoadBalance){
        this.rpcLoadBalance=rpcLoadBalance;
    }

    public static LoadBalance match(String name,LoadBalance defaultRouter){
        for (LoadBalance item:LoadBalance.values()){
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultRouter;
    }

     public static void main(String[] args) {
        String serviceKey = "service";
        TreeSet<String> addressSet = new TreeSet<String>(){{
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
        }};

//        for (LoadBalance item : LoadBalance.values()) {
//            long start = System.currentTimeMillis();
//            for (int i = 0; i < 10000; i++) {
//                String address =item.rpcLoadBalance.route(serviceKey,addressSet);
//                System.out.println(address);
//            }
//            long end = System.currentTimeMillis();
//            System.out.println(item.name() + " --- " + (end-start));
//        }
         LoadBalance lru = LoadBalance.match("LRU", LoadBalance.ROUND);
         System.out.println(lru.name());

     }
}
