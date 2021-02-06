package com.cxl.rpc.remoting.consumer.route.impl;

import com.cxl.rpc.remoting.consumer.route.RpcLoadBalance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RpcLoadBalanceLRUStrategy extends RpcLoadBalance {
    private ConcurrentMap<String, LinkedHashMap<String, String>> concurrentMap = new ConcurrentHashMap<>();
    private long CACHE_VALID_TIME = 0;

    public String doRoute(String serviceKey, TreeSet<String> addressSet) {

        //cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            concurrentMap.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        }
        //init lru
        LinkedHashMap<String, String> lruItem = concurrentMap.get(serviceKey);
        if (lruItem == null) {
            /**
             * LinkedHashMap
             *      a、accessOrder：ture=访问顺序排序（get/put时排序）/ACCESS-LAST；false=插入顺序排期/FIFO；
             *      b、removeEldestEntry：新增元素时将会调用，返回true时会删除最老元素；可封装LinkedHashMap并重写该方法，比如定义最大容量，超出是返回true即可实现固定长度的LRU算法；
             */

            lruItem = new LinkedHashMap<String, String>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    if (super.size() > 1000) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            concurrentMap.put(serviceKey, lruItem);
        }

        //put new
        for (String address : addressSet) {
            if (!lruItem.containsValue(address)) {
                lruItem.put(address, address);
            }
        }

        //remove old
        List<String> delKeys = new ArrayList<>();

        for (String existKey: lruItem.keySet()) {
            if (!addressSet.contains(existKey)) {
                delKeys.add(existKey);
            }
        }
        if (delKeys.size()>0) {
            for (String delkey: delKeys) {
                lruItem.remove(delkey);
            }
        }

        //load

        String eldestKey =lruItem.entrySet().iterator().next().getKey();

        return lruItem.get(eldestKey);
    }

    @Override
    public String route(String serviceKey, TreeSet<String> addressSet) {
        return doRoute(serviceKey,addressSet);
    }
}
