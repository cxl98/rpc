package com.cxl.rpc.remoting.provider;

import com.cxl.rpc.registry.ServiceRegistry;
import com.cxl.rpc.registry.impl.LocalRegistry;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.remoting.net.Server;
import com.cxl.rpc.remoting.net.impl.netty.server.NettyServer;
import com.cxl.rpc.remoting.net.params.BaseCallback;
import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.remoting.net.params.RpcResponse;
import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.serialize.impl.ProtostuffSerializer;
import com.cxl.rpc.util.IpUtil;
import com.cxl.rpc.util.NetUtil;
import com.cxl.rpc.util.RpcException;
import com.cxl.rpc.util.ThrowableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.Map;

public class RpcProviderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProviderFactory.class);

    //------------------------config--------------------------
    private Class<? extends Server> serverClass= NettyServer.class;
    private Serializer serializer = Serializer.SerializerEnum.PROTOSTUFF.getSerializer();

    private int corePoolSize = 60;
    private int maxPoolSize = 300;
    private String ip = null;
    private int port = 8888;
    private String accessToken = null;


    private Class<? extends ServiceRegistry> serviceRegistryClass;
    private Map<String, String> serviceRegistryParam;

    public Class<? extends Server> getServerClass() {
        return serverClass;
    }

    public void setServerClass(Class<? extends Server> serverClass) {
        this.serverClass = serverClass;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
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

    public Serializer getSerializer() {
        return serializer;
    }


    //--------------------start/stop--------------------

    private Server server;
    private ServiceRegistry serviceRegistry;
    private String serviceAddress;

    public void start() throws Exception {
        if (this.ip == null) {
            this.ip = IpUtil.getIp();
        }
        if (NetUtil.isPortUsed(this.port)) {
            throw new RpcException("rpc provider port[" + this.port + "] is used.");
        }
        if (this.serviceRegistryClass != null) {
            if (this.serviceRegistryParam == null) {
                throw new RpcException("rpc provider serviceRegistryParam is Missing.");
            }
        }
        //start server
        serviceAddress = IpUtil.getIpPort(this.ip, port);
        server = serverClass.newInstance();
        server.setStartedCallback(new BaseCallback() {
            @Override
            public void run() throws Exception {
                //start registry
                if (null != serviceRegistryClass) {
                    serviceRegistry = serviceRegistryClass.newInstance();
                    serviceRegistry.start(serviceRegistryParam);
                    if (serviceData.size() > 0) {
                        serviceRegistry.registry(serviceData.keySet(), serviceAddress);
                    }
                }
            }
        });

        server.setStopCallback(new BaseCallback() {
            @Override
            public void run() throws Exception {
                //stop registry
                if (serviceRegistry != null) {
                    if (serviceData.size() > 0) {
                        serviceRegistry.remove(serviceData.keySet(), serviceAddress);
                    }
                    serviceRegistry.stop();
                    serviceRegistry = null;
                }
            }
        });
        server.start(this);
    }

    public void stop() throws Exception {
        server.stop();
    }
    //----------------server invoke---------------------
    /**
     * init local rpc service map
     */
    private Map<String, Object> serviceData = new HashMap<>();

    public Map<String, Object> getServiceData() {
        return serviceData;
    }

    /**
     * make service key
     *
     * @param iface
     * @param version
     * @return
     */
    public static String makeServiceKey(String iface, String version) {
        String serviceKey = iface;
        if (version != null && version.length() > 0) {
            serviceKey += "#".concat(version);
        }
        return serviceKey;
    }

    public void addService(String iface, String version, Object serviceBean) {
        String serviceKey = makeServiceKey(iface, version);
        serviceData.put(serviceKey, serviceBean);

        LOGGER.info(">>>>>>>>>>>rpc, provider factory add service success ,serviceKey = {} , serviceBean ={} ", serviceKey, serviceBean.getClass());
    }

    public RpcResponse invokeService(RpcRequest request) {

        //make response
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(request.getRequestId());

        //match service bean
        String serviceKey = makeServiceKey(request.getClassName(), request.getVersion());

        Object serviceBean = serviceData.get(serviceKey);


        //valid
        if (serviceBean == null) {
            rpcResponse.setErrorMsg("The serviceKey[" + serviceKey + "] not found.");
            return rpcResponse;
        }

        if (System.currentTimeMillis() - request.getCreateMillisTime() > 3 * 60 * 1000) {
            rpcResponse.setErrorMsg("The timestamp difference between admin and executor exceeds the limit.");
            return rpcResponse;
        }

        if (accessToken != null && accessToken.length() > 0 && !accessToken.equals(request.getAccessToken())) {
            rpcResponse.setErrorMsg("the access token[" + request.getAccessToken() + "] is wrong");
            return rpcResponse;
        }

        try {
            //invoke
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);

            Object result = method.invoke(serviceBean, parameters);
            rpcResponse.setResult(result);
        } catch (Throwable e) {
            LOGGER.error("rpc provider invokeService error.", e);
            rpcResponse.setErrorMsg(ThrowableUtil.toString(e));
        }
        return rpcResponse;
    }
}
