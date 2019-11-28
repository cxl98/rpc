package com.cxl.rpc.remoting.provider;

import com.cxl.rpc.registry.ServiceRegistry;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.remoting.net.Server;
import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.IpUtil;
import com.cxl.rpc.util.NetUtil;
import com.cxl.rpc.util.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RpcProviderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProviderFactory.class);

    //------------------------config--------------------------
    private NetEnum netType;
    private Serializer serializer;

    private int corePoolSize;
    private int maxPoolSize;

    private String ip;       //for registry
    private int port;        //default port
    private String accessToken;


    private Class<? extends ServiceRegistry> serviceRegistryClass;
    private Map<String, String> serviceRegistryParam;

    public RpcProviderFactory() {
    }

    public RpcProviderFactory(NetEnum netType, Serializer serializer, int corePoolSize, int maxPoolSize, String ip, int port, String accessToken, Class<? extends ServiceRegistry> serviceRegistryClass, Map<String, String> serviceRegistryParam) {
        //init

        this.netType = netType;
        this.serializer = serializer;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.ip = ip;
        this.port = port;
        this.accessToken = accessToken;
        this.serviceRegistryClass = serviceRegistryClass;
        this.serviceRegistryParam = serviceRegistryParam;

        //valid
        if (this.netType == null) {
            throw new RuntimeException("rpc provider netType missing.");
        }
        if (this.serializer == null) {
            throw new RuntimeException("rpc provider serializer missing.");
        }

        if (!(this.corePoolSize >= 0 && this.maxPoolSize > 0 && this.maxPoolSize >= this.corePoolSize)) {
            this.corePoolSize = 60;
            this.maxPoolSize = 300;
        }
        if (this.ip == null) {
            this.ip = IpUtil.getIp();
        }
        if (this.port <= 0) {
            this.port = 7080;
        }
        if (NetUtil.isPortUsed(this.port)) {
            throw new RpcException("rpc provider port[" + this.port + "] is used.");
        }
        if (this.serviceRegistryClass != null) {
            if (this.serviceRegistryParam == null) {
                throw new RpcException("rpc provider serviceRegistryParam is Missing.");
            }
        }
    }


    public Serializer getSerializer() {
        return serializer;
    }

    public int getPort() {
        return port;
    }

    public  int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    //--------------------start/stop--------------------

    private Server server;
    private ServiceRegistry serviceRegistry;
    private String serviceAddress;



}
