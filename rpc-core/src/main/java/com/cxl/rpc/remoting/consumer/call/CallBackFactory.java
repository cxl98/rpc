package com.cxl.rpc.remoting.consumer.call;

import com.cxl.rpc.remoting.consumer.call.impl.CallBackStrategy;
import com.cxl.rpc.remoting.consumer.call.impl.FutureCallStrategy;
import com.cxl.rpc.remoting.consumer.call.impl.OneWayCallStrategy;
import com.cxl.rpc.remoting.consumer.call.impl.SyncCallStrategy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CallBackFactory {
    private static CallBackFactory instance=new CallBackFactory();
    private CallBackFactory(){}
    private static ConcurrentMap<String,CallBack> map=new ConcurrentHashMap<>();
    static {
        map.put(CallType.CALLBACK.name(),new CallBackStrategy());
        map.put(CallType.SYNC.name(),new SyncCallStrategy());
        map.put(CallType.FUTURE.name(),new FutureCallStrategy());
        map.put(CallType.ONEWAY.name(),new OneWayCallStrategy());
    }
    public CallBack create(String type){
        return map.get(type);
    }

    public static CallBackFactory getInstance() {
        return instance;
    }
}
