package com.cxl.rpc.remoting.invoker.reference;

import com.cxl.rpc.remoting.invoker.RpcInvokerFactory;
import com.cxl.rpc.remoting.invoker.call.CallType;
import com.cxl.rpc.remoting.invoker.call.RpcInvokeCallback;
import com.cxl.rpc.remoting.invoker.call.RpcInvokeFuture;
import com.cxl.rpc.remoting.invoker.generic.RpcGenericService;
import com.cxl.rpc.remoting.invoker.route.LoadBalance;
import com.cxl.rpc.remoting.net.Client;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.remoting.net.params.RpcFutureResponse;
import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.remoting.net.params.RpcResponse;
import com.cxl.rpc.remoting.provider.RpcProviderFactory;
import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.ClassUtil;
import com.cxl.rpc.util.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RpcReferenceBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcReferenceBean.class);

    //-------------------config-------------------
    private NetEnum netType;
    private Serializer serializer;
    private CallType callType;
    private LoadBalance loadBalance;

    private Class<?> iface;
    private String version;

    private long timeout = 1000;

    private String address;
    private String accessToken;

    private RpcInvokeCallback invokeCallback;

    private RpcInvokerFactory invokerFactory;

    public RpcReferenceBean(NetEnum netType, Serializer serializer, CallType callType, LoadBalance loadBalance, Class<?> iface, String version, long timeout, String address, String accessToken, RpcInvokeCallback rpcInvokeCallback, RpcInvokerFactory invokerFactory) {
        this.netType = netType;
        this.serializer = serializer;
        this.callType = callType;
        this.loadBalance = loadBalance;
        this.iface = iface;
        this.version = version;
        this.timeout = timeout;
        this.address = address;
        this.accessToken = accessToken;
        this.invokeCallback = rpcInvokeCallback;
        this.invokerFactory = invokerFactory;
        // init Client
        initClient();
    }

    //get
    public Serializer getSerializer() {
        return serializer;
    }

    public long getTimeout() {
        return timeout;
    }

    public RpcInvokerFactory getInvokerFactory() {
        return invokerFactory;
    }

    // ---------------------- initClient ----------------------
    Client client = null;

    private void initClient() {
        try {
            client = netType.clientClass.newInstance();
            client.init(this);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RpcException(e);
        }
    }


    //---------------------util-----------------

    public Object getObject() {
        return Proxy.newProxyInstance(Thread.currentThread()
                .getContextClassLoader(), new Class[]{iface}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //method param
                String className = method.getDeclaringClass().getName();//iface.getName
                String version1 = version;
                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                Object[] parameters = args;


                //filter for generic
                if (className.equals(RpcGenericService.class.getName()) && methodName.equals("invoke")) {
                    Class<?>[] paramTpes = null;
                    if (args[3] != null) {
                        String[] paramTypes_str = (String[]) args[3];
                        if (paramTypes_str.length > 0) {
                            parameters = new Class[paramTypes_str.length];

                            for (int i = 0; i < paramTypes_str.length; i++) {
                                parameters[i] = ClassUtil.resolveClass(paramTypes_str[i]);
                            }
                        }
                    }
                    className = (String) args[0];
                    version1 = (String) args[1];
                    methodName = (String) args[2];
                    parameterTypes = paramTpes;
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
                        if (addressSet == null || addressSet.size() == 0) {

                        } else if (addressSet.size() == 1) {
                            finalAddress = addressSet.first();
                        } else {
                            finalAddress = loadBalance.rpcLoadBalance.route(serviceKey, addressSet);
                        }
                    }
                }
                if (finalAddress == null || finalAddress.length() == 0) {
                    throw new RpcException("rpc referencebean[" + className + "]address empty");
                }

                //request
                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString());

                request.setCreateMillisTime(System.currentTimeMillis());
                request.setAccessToken(accessToken);
                request.setClassName(className);
                request.setMethodName(methodName);
                request.setParameterTypes(parameterTypes);
                request.setParameters(parameters);

                //send
                if (CallType.SYNC == callType) {
                    //future-response set
                    RpcFutureResponse futureResponse = new RpcFutureResponse(invokerFactory, request, null);

                    try {
                        //do invoke
                        client.asyncSend(finalAddress, request);


                        //future get
                        RpcResponse response = futureResponse.get(timeout, TimeUnit.MILLISECONDS);
                        if (response.getErrorMsg() != null) {
                            throw new RpcException(response.getErrorMsg());
                        }
                        return response.getResult();
                    } catch (Exception e) {
                        LOGGER.info(">>>>>>>>>>>>>>>rpc ,invoke error , address:{}, rpcRequest:{}", finalAddress, request);
                        throw (e instanceof RpcException) ? e : new RpcException(e);
                    } finally {
                        //future-response remove
                        futureResponse.removeInvokerFuture();
                    }
                } else if (CallType.FUTURE == callType) {
                    //future-response set
                    RpcFutureResponse futureResponse = new RpcFutureResponse(invokerFactory, request, null);

                    try {
                        //invoke future set
                        RpcInvokeFuture invokeFuture = new RpcInvokeFuture(futureResponse);
                        RpcInvokeFuture.setFuture(invokeFuture);


                        //do invoke
                        client.asyncSend(finalAddress, request);
                        return null;
                    } catch (Exception e) {
                        LOGGER.info(">>>>>>>>>>>>>>>>>>>>rpc, invoke error , address:{}, RpcRequest{}", finalAddress, request);

                        //future-response remove
                        futureResponse.removeInvokerFuture();
                        throw (e instanceof RpcException) ? e : new RpcException(e);
                    }
                } else if (CallType.CALLBACK == callType) {
                    //get callback
                    RpcInvokeCallback finalInvokeCallback = invokeCallback;
                    RpcInvokeCallback threadInvokeCallback = RpcInvokeCallback.getCallback();

                    if (threadInvokeCallback != null) {
                        finalInvokeCallback = threadInvokeCallback;
                    }
                    if (finalInvokeCallback == null) {
                        throw new RpcException("rpc RpcInvokeCallback CallType=" + CallType.CALLBACK.name() + ") cannot be null.");
                    }

                    //future-response set
                    RpcFutureResponse futureResponse = new RpcFutureResponse(invokerFactory, request, finalInvokeCallback);

                    try {
                        client.asyncSend(finalAddress, request);
                    } catch (Exception e) {
                        LOGGER.info(">>>>>>>>>>>>>>rpc , invoke error , address:{}, RpcRequest{}", finalAddress, request);

                        //future-response remove
                        futureResponse.removeInvokerFuture();

                        throw (e instanceof RpcException) ? e : new RpcException(e);
                    }
                    return null;
                }else if (CallType.ONEWAY==callType){
                    client.asyncSend(finalAddress,request);
                    return null;
                }else{
                    throw new RpcException("rpc callType["+callType+"] invalid");
                }
            }
        });
    }

    public Class<?> getObjectType(){
        return iface;
    }
}
