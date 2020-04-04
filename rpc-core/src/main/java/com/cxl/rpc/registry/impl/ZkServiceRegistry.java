package com.cxl.rpc.registry.impl;

import com.cxl.rpc.registry.ServiceRegistry;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ZkServiceRegistry extends ServiceRegistry {


    @Override
    public void start(Map<String, String> param) {

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
