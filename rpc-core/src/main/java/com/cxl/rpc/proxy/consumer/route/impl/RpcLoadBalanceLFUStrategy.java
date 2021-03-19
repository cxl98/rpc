package com.cxl.rpc.proxy.consumer.route.impl;

import com.cxl.rpc.proxy.consumer.route.RpcLoadBalance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RpcLoadBalanceLFUStrategy  extends RpcLoadBalance {
    private ConcurrentMap<String, HashMap<String,Integer>> concurrentMap=new ConcurrentHashMap<>();
    private long CACHE_VALID_TIME=0;

    public String doRoute(String serviceKey,TreeSet<String> addressSet){

        //cache clear
        if (System.currentTimeMillis()>CACHE_VALID_TIME) {
            concurrentMap.clear();
            CACHE_VALID_TIME=System.currentTimeMillis()+24*60*60*1000;
        }

        //lfu item init

        HashMap<String,Integer> lfuItemMap=concurrentMap.get(serviceKey);
        // Key排序可以用TreeMap+构造入参Compare；Value排序暂时只能通过ArrayList；

        if (lfuItemMap==null) {
            lfuItemMap=new HashMap<>();
            concurrentMap.putIfAbsent(serviceKey,lfuItemMap);// 避免重复覆盖
        }

        //put new
        for (String address: addressSet) {
            if (!lfuItemMap.containsKey(address)||lfuItemMap.get(address)>1000000) {
                lfuItemMap.put(address,0);
            }
        }

        //remove old

        List<String> delKeys=new ArrayList<>();

        for (String existKey: lfuItemMap.keySet()) {
            if (!addressSet.contains(existKey)) {
                delKeys.add(existKey);
            }
        }

        if (delKeys.size()>0) {
            for (String delKey: delKeys) {
                lfuItemMap.remove(delKey);
            }
        }

        //load least userd count address
        List<Map.Entry<String,Integer>> lfuItemList=new ArrayList<>(lfuItemMap.entrySet());

        Collections.sort(lfuItemList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        Map.Entry<String,Integer> addressItem=lfuItemList.get(0);
        String minAddress=addressItem.getKey();
        addressItem.setValue(addressItem.getValue()+1);

        return minAddress;
    }
    @Override
    public String route(String serviceKey, TreeSet<String> addressSet) {

        return doRoute(serviceKey,addressSet);
    }
}
