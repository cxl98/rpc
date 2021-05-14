package com.cxl.rpc.registry;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * service  registry
 *
 *          - key01(service01)
 *              -value01 (ip:port01)
 *              -value02 (ip:port02)
 */
public abstract class ServiceRegistry {
    /**
     *
     * start
     */
    public abstract void start(Map<String,String> param);

    /**
     * stop
     */
    public abstract void stop();

    /**
     * registry service for mult
     * @param keys
     * @param value
     * @return
     */
    public abstract boolean registry(Set<String> keys,String value);

    /**
     * remove service for mult
     * @param keys
     * @param value
     * @return
     */
    public abstract boolean remove(Set<String> keys,String value);

    /**
     * discovery service for mult
     * @param keys
     * @return
     */
    public abstract Map<String, TreeSet<String>> discovery(Set<String> keys);

    /**
     * discovery service for one
     * @param key
     * @return
     */
    public abstract TreeSet<String> discovery(String key);


}
