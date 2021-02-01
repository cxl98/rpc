package com.cxl.rpc.remoting.consumer.route.impl;

import com.cxl.rpc.remoting.consumer.route.RpcLoadBalance;

import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RpcLoadBalanceRoundStrategy extends RpcLoadBalance {
    private final ConcurrentMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
    /**
     * 缓存有效时间
     */
    private long cvt = 0;

    private int count(String serviceKey) {
        //cache clear
        if (System.currentTimeMillis() > cvt) {
            concurrentMap.clear();
            cvt = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        }

        //count++
        Integer count = concurrentMap.get(serviceKey);
        count = (count == null || count > 1000000) ? (new Random().nextInt(100)) : ++count;
        concurrentMap.put(serviceKey, count);
        return count;
    }

    @Override
    public String route(String serviceKey, TreeSet<String> addressSet) {
        //arr
        String[] addressArr = addressSet.toArray(new String[addressSet.size()]);

        //round
        return addressArr[count(serviceKey) % addressArr.length];
    }
}
