package com.cxl.rpc.remoting.consumer.route.impl;

import com.cxl.rpc.remoting.consumer.route.RpcLoadBalance;

import java.util.Random;
import java.util.TreeSet;

public class RpcLoadBalanceRandomStrategy extends RpcLoadBalance {
    private Random random=new Random();
    @Override
    public String route(String serviceKey, TreeSet<String> addressSet) {
        //arr
        String[] addressArr=addressSet.toArray(new String[addressSet.size()]);

        //random
        String finalAddress=addressArr[random.nextInt(addressSet.size())];
        return finalAddress;
    }
}
