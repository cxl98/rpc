package com.cxl.rpc.registry.impl;

import com.cxl.rpc.registry.ServiceRegistry;
import com.cxl.rpc.util.ZkClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ZkServiceRegistry extends ServiceRegistry {
    private static final Logger LOGGER= LoggerFactory.getLogger(ZkServiceRegistry.class);

    private static final String ENV="env";
    private static final String ZK_ADDRESS="address";
    private static final String Zk_DIGEST="digest";

    private String zkEnvPath;
    private ZkClientUtil zkClient=null;

    private Thread refreshThread;
    private volatile boolean refreshThreadStop=false;

    private  ConcurrentMap<String,TreeSet<String>> registryData=new ConcurrentHashMap<>();
    private  ConcurrentMap discoveryData=new ConcurrentHashMap();

    public String keyToPath(String nodeKey){
        return "/"+nodeKey;
    }

    @Override
    public void start(Map<String, String> param) {
        String address=param.get(ZK_ADDRESS);
        String digest = param.get(Zk_DIGEST);
        String env = param.get(ENV);

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean registry(Set<String> keys, String value) {
        return false;
    }

    @Override
    public boolean remove(Set<String> keys, String value) {
        return false;
    }

    @Override
    public Map<String, TreeSet<String>> discovery(Set<String> keys) {
        return null;
    }

    @Override
    public TreeSet<String> discovery(String key) {
        return null;
    }
}
