package com.cxl.rpc.remoting.consumer.route.impl;

import com.cxl.rpc.remoting.consumer.route.RpcLoadBalance;

import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RpcLoadBalanceRoundStrategy extends RpcLoadBalance {
    private ConcurrentMap<String,Integer> concurrentMap=new ConcurrentHashMap<>();
    private long CACHE_VALID_TIME=0;

    private int count(String serviceKey){
        //cache clear
        if (System.currentTimeMillis()>CACHE_VALID_TIME){
            concurrentMap.clear();
            CACHE_VALID_TIME=System.currentTimeMillis()+24*60*60*1000;
        }

        //count++
        Integer count=concurrentMap.get(serviceKey);
        count=(count==null||count>1000000)?(new Random().nextInt(100)):++count;
        concurrentMap.put(serviceKey,count);
        return count;
    }
    @Override
    public String route(String serviceKey, TreeSet<String> addressSet) {
        //arr
        String[] addressArr=addressSet.toArray(new String[addressSet.size()]);

        //round
        String finalAddress=addressArr[count(serviceKey)%addressArr.length];
        return finalAddress;
    }
}
