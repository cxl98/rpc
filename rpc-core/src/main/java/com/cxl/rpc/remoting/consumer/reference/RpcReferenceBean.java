package com.cxl.rpc.remoting.consumer.reference;

import com.cxl.rpc.remoting.consumer.RpcInvokerFactory;
import com.cxl.rpc.remoting.consumer.call.CallBack;
import com.cxl.rpc.remoting.consumer.call.CallBackFactory;
import com.cxl.rpc.remoting.consumer.call.CallType;
import com.cxl.rpc.remoting.consumer.generic.RpcGenericService;
import com.cxl.rpc.remoting.consumer.route.LoadBalance;
import com.cxl.rpc.remoting.net.Client;
import com.cxl.rpc.remoting.net.impl.netty.client.NettyClient;
import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.remoting.provider.RpcProviderFactory;
import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.ClassUtil;
import com.cxl.rpc.util.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.TreeSet;
import java.util.UUID;

public class RpcReferenceBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcReferenceBean.class);

    private Class<? extends Client> clientClass =NettyClient.class;
    private Serializer serializer = Serializer.SerializerEnum.PROTOSTUFF.getSerializer();
    private CallType callType = CallType.SYNC;
    private LoadBalance loadBalance = LoadBalance.ROUND;

    private Class<?> iface;
    private String version;

    private long timeout = 1000;

    private String address;
    private String accessToken;

//    private RpcInvokeCallback invokeCallback;

    private RpcInvokerFactory invokerFactory;

    private CallBackFactory callBackFactory;

    public Class<? extends Client> getClientClass() {
        return clientClass;
    }

    public void setClientClass(Class<? extends Client> clientClass) {
        this.clientClass = clientClass;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public CallType getCallType() {
        return callType;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    public LoadBalance getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    public Class<?> getIface() {
        return iface;
    }

    public void setIface(Class<?> iface) {
        this.iface = iface;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public void setInvokerFactory(RpcInvokerFactory invokerFactory) {
        this.invokerFactory = invokerFactory;
    }


    public RpcReferenceBean() {
    }

    //get序列化方式
    public Serializer getSerializer() {
        return serializer;
    }

    public long getTimeout() {
        return timeout;
    }

    public RpcInvokerFactory getInvokerFactory() {
        return invokerFactory;
    }

    // ---------------------- initClient(初始化clinet) ----------------------
    private Client client = null;

    public Client getClient() {
        return client;
    }

    /**
     * 初始化clinet
     * 检验参数等等
     */
    public void init() {
        if (null == this.clientClass) {
            throw new RpcException("rpc reference netType missing.");
        }
        if (null == this.serializer) {
            throw new RpcException("rpc reference serializer missing.");
        }
        if (null == this.callType) {
            throw new RpcException("rpc reference callType missing.");
        }
        if (null == this.loadBalance) {
            throw new RpcException("rpc reference loadBalance missing.");
        }
        if (0 >= this.timeout) {
            this.timeout = 1000;
        }

        if (null == this.invokerFactory) {
            this.invokerFactory = RpcInvokerFactory.getInstance();
        }
        if (null == this.callBackFactory) {
            callBackFactory = CallBackFactory.getInstance();
        }
        try {
            client = clientClass.newInstance();
            client.init(this);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RpcException(e);
        }
    }


    //动态代理

    public Object getObject() {

        return Proxy.newProxyInstance(Thread.currentThread()
                .getContextClassLoader(), new Class[]{iface}, (proxy, method, args) -> {
            //method param
            String className = method.getDeclaringClass().getName();//iface.getName

            String version1 = version;//分布式中版本号要带上
            String methodName = method.getName();

            Class<?>[] parameterTypes = method.getParameterTypes();

            Object[] parameters = args;


            //filter for generic
            if (className.equals(RpcGenericService.class.getName()) && "invoke".equals(methodName)) {
                Class<?>[] paramTypes = null;
                if (args[3] != null) {
                    String[] paramTypes_str = (String[]) args[3];
                    if (paramTypes_str.length > 0) {
                        paramTypes = new Class[paramTypes_str.length];

                        for (int i = 0; i < paramTypes_str.length; i++) {
                            paramTypes[i] = ClassUtil.resolveClass(paramTypes_str[i]);
                        }
                    }
                }
                className = (String) args[0];
                version1 = (String) args[1];
                methodName = (String) args[2];
                parameterTypes = paramTypes;
                parameters = (Object[]) args[4];
            }
            //filter method like "Object.toString()"
            if (className.equals(Object.class.getName())) {
                LOGGER.info(">>>>>>>>>>>>>>>>>>>>>rpc proxy class-method not support [{}#{}]", className, methodName);
                throw new RpcException("rpc proxy class-method not support");
            }

            //address
            String finalAddress = address;
            if (finalAddress == null || finalAddress.length() == 0) {
                if (invokerFactory != null && invokerFactory.getServiceRegistry() != null) {
                    //discovery
                    String serviceKey = RpcProviderFactory.makeServiceKey(className, version1);
                    TreeSet<String> addressSet = invokerFactory.getServiceRegistry().discovery(serviceKey);

                    //load balance
                    if (addressSet.size() == 1) {
                        finalAddress = addressSet.first();
                        address = finalAddress;
                    } else {
                        finalAddress = loadBalance.rpcLoadBalance.route(serviceKey, addressSet);
                        address=finalAddress;
                    }
                }
            }
            if (finalAddress == null || finalAddress.length() == 0) {
                throw new RpcException("rpc rpcReferenceBean[" + className + "]address empty");
            }

            //request
            RpcRequest rpcRequest = doRequest(className, methodName, parameterTypes, parameters);
            CallBack back = callBackFactory.create(callType.name());
            Object export = back.export(rpcRequest, this);
            if (null != export) {
                return export;
            }
            return null;
        });
    }
    private RpcRequest doRequest(String className,String methodName,Class<?>[] parameterTypes,Object[] parameters){
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());

        request.setCreateMillisTime(System.currentTimeMillis());
        request.setAccessToken(accessToken);
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameterTypes(parameterTypes);
        request.setParameters(parameters);
        return request;
    }

    public Class<?> getObjectType() {
        return iface;
    }
}
