package com.cxl.rpc.remoting.invoker.reference;

import com.cxl.rpc.remoting.invoker.RpcInvokerFactory;
import com.cxl.rpc.remoting.invoker.call.CallType;
import com.cxl.rpc.remoting.invoker.call.RpcInvokeCallback;
import com.cxl.rpc.remoting.invoker.route.LoadBalance;
import com.cxl.rpc.remoting.net.Client;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcReferenceBean {
    private static final Logger LOGGER= LoggerFactory.getLogger(RpcReferenceBean.class);

    //-------------------config-------------------
    private NetEnum netType;
    private Serializer serializer;
    private CallType callType;
    private LoadBalance loadBalance;

    private Class<?> iface;
    private String version;

    private long timeout=1000;

    private String address;
    private String accessToken;

    private RpcInvokeCallback rpcInvokeCallback;

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
        this.rpcInvokeCallback = rpcInvokeCallback;
        this.invokerFactory = invokerFactory;
        // init Client
        initClient();
    }
    //get
    public Serializer getSerializer(){
        return serializer;
    }
    public long getTimeout(){
        return timeout;
    }

    public RpcInvokerFactory getInvokerFactory(){
        return invokerFactory;
    }

    // ---------------------- initClient ----------------------
    Client client=null;
    private void initClient() {
        try {
            client=netType.clientClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RpcException(e);
        }
    }
}
