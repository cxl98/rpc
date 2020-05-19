package com.cxl.rpc.remoting.consumer.reference.impl;

import com.cxl.rpc.remoting.consumer.reference.RpcReferenceBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class RpcSpringReferenceBean implements FactoryBean<Object>, InitializingBean {

    private RpcReferenceBean rpcReferenceBean;

    //--------------------------util--------------------------
    @Override
    public void afterPropertiesSet() throws Exception {
        rpcReferenceBean=new RpcReferenceBean();
    }

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
}
