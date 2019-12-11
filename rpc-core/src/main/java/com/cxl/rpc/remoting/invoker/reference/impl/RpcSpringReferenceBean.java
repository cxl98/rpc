package com.cxl.rpc.remoting.invoker.reference.impl;

import com.cxl.rpc.remoting.invoker.RpcInvokerFactory;
import com.cxl.rpc.remoting.invoker.call.CallType;
import com.cxl.rpc.remoting.invoker.call.RpcInvokeCallback;
import com.cxl.rpc.remoting.invoker.reference.RpcReferenceBean;
import com.cxl.rpc.remoting.invoker.route.LoadBalance;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.serialize.Serializer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class RpcSpringReferenceBean implements FactoryBean<Object>, InitializingBean {

    //-----------------config--------------------------
    private String netType= NetEnum.NETTY.name();
    private String serialize= Serializer.SerializerEnum.JACKSON.name();
    private String callType= CallType.SYNC.name();
    private String loadBalance= LoadBalance.ROUND.name();

    private Class<?> iface;
    private String version;

    private long timeout=1000;

    private String address;
    private String accessToken;

    private RpcInvokeCallback invokeCallback;

    private RpcInvokerFactory invokerFactory;

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public void setSerializer(String serializer) {
        this.serialize = serializer;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public void setLoadBalance(String loadBalance) {
        this.loadBalance = loadBalance;
    }

    public void setIface(Class<?> iface) {
        this.iface = iface;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setInvokeCallback(RpcInvokeCallback invokeCallback) {
        this.invokeCallback = invokeCallback;
    }

    public void setInvokerFactory(RpcInvokerFactory invokerFactory) {
        this.invokerFactory = invokerFactory;
    }

    //------------------init---------------------
    private RpcReferenceBean rpcReferenceBean;
    private void init(){

        //prepare config

        NetEnum netTypeEnum=NetEnum.autoMatch(netType,null);
        Serializer.SerializerEnum serializerEnum=Serializer.SerializerEnum.match(serialize,null);
        Serializer serializer=serializerEnum!=null?serializerEnum.getSerializer():null;
        CallType callTypeEnum=CallType.match(callType,null);
        LoadBalance loadBalanceEnum=LoadBalance.match(loadBalance,null);

        //init config
        rpcReferenceBean=new RpcReferenceBean(netTypeEnum,serializer,callTypeEnum,loadBalanceEnum,iface,version,timeout,address,accessToken,invokeCallback,invokerFactory);
    }

    //--------------------------util--------------------------
    @Override
    public Object getObject() throws Exception {
        return rpcReferenceBean.getObject();
    }

    @Override
    public Class<?> getObjectType() {
        return rpcReferenceBean.getObjectType();
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
