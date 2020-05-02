package com.cxl.rpc.registry.impl;

import com.cxl.registry.client.RegistryClient;
import com.cxl.registry.client.model.RegistryDataParamVo;
import com.cxl.rpc.registry.ServiceRegistry;

import java.util.*;

/**
 * service registry for registry
 */
public class RegistryServiceRegistry extends ServiceRegistry {
    public static final String REGISTRY_ADDRESS="REGISTRY_ADDRESS";
    public static final String ACCESS_TOKEN="ACCESS_TOKEN";
    public static final String BIZ="BIZ";
    public static final String ENV="ENV";
    
    private RegistryClient registryClient;
    

    @Override
    public void start(Map<String, String> param) {
        String registryAddress=param.get(REGISTRY_ADDRESS);
        String accessToken=param.get(ACCESS_TOKEN);
        String biz=param.get(BIZ);
        String env=param.get(ENV);

        biz = (biz != null&&biz.length()>0)?biz:"default";
        env=(env!=null&&env.length()>0)?env:"default";
        registryClient=new RegistryClient(registryAddress,accessToken,biz,env);
    }

    @Override
    public void stop() {
        if (registryClient != null) {
            registryClient.stop();
        }
    }

    @Override
    public boolean registry(Set<String> keys, String value) {
        if (keys==null||keys.size()==0||value==null||value.length()==0) {
            return false;
        }
        
        //init
        List<RegistryDataParamVo> registryDataList=new ArrayList<>();
        for (String key: keys) {
            registryDataList.add(new RegistryDataParamVo(key,value));
        }
        return registryClient.registry(registryDataList);
    }

    @Override
    public boolean remove(Set<String> keys, String value) {
        if (keys==null||keys.size()==0||value==null||value.length()==0) {
            return false;
        }
        
        //init
        List<RegistryDataParamVo> registryDataList=new ArrayList<>();
        for (String key: keys) {
            registryDataList.add(new RegistryDataParamVo(key,value));
        }
        return registryClient.remove(registryDataList);
    }

    @Override
    public Map<String, TreeSet<String>> discovery(Set<String> keys) {
      
        return registryClient.discovery(keys);
        
    }

    @Override
    public TreeSet<String> discovery(String key) {
        return registryClient.discovery(key);
    }
}
