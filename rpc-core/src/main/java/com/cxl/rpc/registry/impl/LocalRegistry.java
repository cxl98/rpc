package com.cxl.rpc.registry.impl;

import com.cxl.rpc.registry.ServiceRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * service registry for "local"
 */
public class LocalRegistry extends ServiceRegistry {

    /**
     * registry data
     * @param param
     */
    private Map<String,TreeSet<String>> registryData;

    @Override
    public void start(Map<String, String> param) {
        registryData=new HashMap<>();
    }

    @Override
    public void stop() {
            registryData.clear();
    }

    @Override
    public boolean registry(Set<String> keys, String value) {
        if (keys==null || keys.size()==0 ||value==null ||value.length()==0) {
            return false;
        }
        for (String key: keys) {
            TreeSet<String> values=registryData.get(key);

            if (values==null) {
                values=new TreeSet<>();
                registryData.put(key,values);
            }
        }
        return true;
    }

    @Override
    public boolean remove(Set<String> keys, String value) {
        if (keys==null||keys.size()==0||value==null||value.length()==0) {
            return false;
        }
        for (String key: keys) {
            TreeSet<String> values=registryData.get(key);
            if (values != null) {
                values.remove(value);
            }
        }
        return true;
    }

    @Override
    public Map<String, TreeSet<String>> discovery(Set<String> keys) {
        if (keys==null||keys.size()==0) {
            return null;
        }
        Map<String,TreeSet<String>> registryDataTmp=new HashMap<>();

        for (String key: keys) {
            TreeSet<String> valueSetTmp=discovery(key);
            if (valueSetTmp != null) {
                registryDataTmp.put(key,valueSetTmp);
            }
        }
        return null;
    }

    @Override
    public TreeSet<String> discovery(String key) {
        return registryData.get(key);
    }
}
