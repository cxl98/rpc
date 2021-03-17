package com.cxl.rpc.proxy.consumer.route.impl;

import com.cxl.rpc.proxy.consumer.route.RpcLoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class RpcLoadBalanceConsistentHashStrategy extends RpcLoadBalance {
    /**
     * 虚拟节点的复制倍数
     */
   private final int VIRTUAL_NODE_NUM=32;

    /**
     * get hash code on 2^32 ring (md5散列的方式计算hash值)
     * @param key 从进来的key
     * @return
     */
   private long hash(String key){

       //md5 byte
       MessageDigest md5;

       try {
           md5=MessageDigest.getInstance("MD5");
       } catch (NoSuchAlgorithmException e) {
           throw new RuntimeException("MD5 not supported",e);
       }
       md5.reset();
       byte[] keyBytes;
       keyBytes=key.getBytes(StandardCharsets.UTF_8);
       md5.update(keyBytes);
       byte[] digest=md5.digest();

       // 哈希码，截断为32位
       long hashCode=((long)(digest[3] & 0xFF)<<24) |((long)(digest[2]& 0xFF)<<16) |((long)(digest[1] & 0xFF)<<8)|(digest[0] &0xFF);
       return hashCode &0xffffffffL;
   }

   public String doRoute(String serviceKey,TreeSet<String> addressSet){
       //虚拟节点　 哈希值 => 物理节点
       TreeMap<Long,String> addressRing=new TreeMap<>();

       for (String address: addressSet) {
           for (int i = 0; i <VIRTUAL_NODE_NUM ; i++) {
               long addressHash=hash(address+"_"+i);
               addressRing.put(addressHash,address);
           }
       }

       long hash=hash(serviceKey);
       SortedMap<Long,String> hashRing=addressRing.tailMap(hash);
       if (!hashRing.isEmpty()) {
           return hashRing.get(hashRing.firstKey());
       }
       return addressRing.firstEntry().getValue();
   }

    // 32位的 Fowler-Noll-Vo 哈希算法 快了4倍
    private Long FNVHash(String key) {
        final int p = 16777619;
        long hash = 21666136261L;
        for (int i = 0, num = key.length(); i < num; ++i) {
            hash = (hash ^ key.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }
    @Override
    public String route(String serviceKey, TreeSet<String> addressSet) {
        return doRoute(serviceKey,addressSet);
    }

}
