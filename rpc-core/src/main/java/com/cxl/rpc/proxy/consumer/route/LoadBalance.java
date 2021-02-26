package com.cxl.rpc.proxy.consumer.route;

import com.cxl.rpc.proxy.consumer.route.impl.*;

import java.util.TreeSet;

public enum  LoadBalance {
    /**
     * 随机算法
     */
    RANDOM(new RpcLoadBalanceRandomStrategy()),
    /**
     *轮询算法
     */
    ROUND(new RpcLoadBalanceRoundStrategy()),
    /**
     * LRU算法
     */
    LRU(new RpcLoadBalanceLRUStrategy()),
    /**
     * LFU算法
     */
    LFU(new RpcLoadBalanceLFUStrategy()),
    /**
     * 一致性hash算法
     */
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
            add("192.168.1.0:8080");
            add("192.168.1.1:8080");
            add("192.168.1.2:8080");
            add("192.168.1.3:8080");
            add("192.168.1.4:8080");
        }};


            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                String address =LoadBalance.match("ROUND",LoadBalance.ROUND).rpcLoadBalance.route(serviceKey,addressSet);
                System.out.println(address);
            }
            long end = System.currentTimeMillis();
            System.out.println((end-start));

//         LoadBalance lru = LoadBalance.match("CONSISTENT_HASH", LoadBalance.ROUND);
//         System.out.println(lru.name());

     }
}
