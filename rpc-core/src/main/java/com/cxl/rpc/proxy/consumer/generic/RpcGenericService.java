package com.cxl.rpc.proxy.consumer.generic;

public interface RpcGenericService {
    /**
     * generic invoke
     *
     * @param iface                 iface name(接口名)
     * @param version               iface version(版本号)作用是为了保证不同版本不会冲突
     * @param method                method name(方法名)
     * @param parameterTypes        parameter types, limit base type like "int、java.lang.Integer、java.util.List、java.util.Map ..."
     * @param args
     * @return object
     */
    Object invoke(String iface,String version,String method,String[] parameterTypes,Object[] args);
}
