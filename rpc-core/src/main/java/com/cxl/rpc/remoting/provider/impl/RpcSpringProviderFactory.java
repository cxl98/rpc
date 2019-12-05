package com.cxl.rpc.remoting.provider.impl;

import com.cxl.rpc.registry.ServiceRegistry;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.remoting.provider.RpcProviderFactory;
import com.cxl.rpc.remoting.provider.annotation.RpcService;
import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.RpcException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class RpcSpringProviderFactory extends RpcProviderFactory implements ApplicationContextAware, InitializingBean, DisposableBean {
    //---------------------config------------------------
    private String netTyp= NetEnum.NETTY.name();
    private String serialize = Serializer.SerializerEnum.JACKSON.name();

    private int corePoolSize;
    private int maxPoolSize;

    private String ip;
    private int port;

    private String accessToken;

    private Class<? extends ServiceRegistry> serviceRegistryClass;
    private Map<String,String> serviceRegistryParam;

    public void setNetTyp(String netTyp) {
        this.netTyp = netTyp;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public String getNetTyp() {
        return netTyp;
    }

    public String getSerialize() {
        return serialize;
    }

    @Override
    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    @Override
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Class<? extends ServiceRegistry> getServiceRegistryClass() {
        return serviceRegistryClass;
    }

    public void setServiceRegistryClass(Class<? extends ServiceRegistry> serviceRegistryClass) {
        this.serviceRegistryClass = serviceRegistryClass;
    }

    public Map<String, String> getServiceRegistryParam() {
        return serviceRegistryParam;
    }

    public void setServiceRegistryParam(Map<String, String> serviceRegistryParam) {
        this.serviceRegistryParam = serviceRegistryParam;
    }
    private void prepareConfig(){
        //prepare config
        NetEnum netTypeEnum=NetEnum.autoMatch(netTyp,null);

        Serializer.SerializerEnum serializerEnum=Serializer.SerializerEnum.match(serialize,null);

        Serializer serializer=serializerEnum!=null?serializerEnum.getSerializer():null;

        //init config
        super.initConfig(netTypeEnum,serializer,corePoolSize,maxPoolSize,ip,port,accessToken,serviceRegistryClass,serviceRegistryParam);

    }
    @Override
    public void destroy() throws Exception {
        super.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.prepareConfig();
        this.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> serviceBeanMap=applicationContext.getBeansWithAnnotation(RpcService.class);
        if (serviceBeanMap != null&&serviceBeanMap.size()>0) {
            for (Object serviceBean: serviceBeanMap.values()) {
                //valid
                if (serviceBean.getClass().getInterfaces().length==0) {
                    throw new RpcException("rpc ,service(RpcService) must inherit interface.");
                }
                //add service
                RpcService rpcService=serviceBean.getClass().getAnnotation(RpcService.class);

                String iface=serviceBean.getClass().getInterfaces()[0].getName();
                String version=rpcService.version();
                super.addService(iface,version,serviceBean);
            }
        }
    }
}
