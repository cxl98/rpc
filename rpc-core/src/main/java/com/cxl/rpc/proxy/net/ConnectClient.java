package com.cxl.rpc.proxy.net;

import com.cxl.rpc.proxy.consumer.RpcInvokerFactory;
import com.cxl.rpc.proxy.consumer.reference.RpcReferenceBean;
import com.cxl.rpc.proxy.net.params.BaseCallback;
import com.cxl.rpc.proxy.net.params.RpcRequest;
import com.cxl.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class ConnectClient {
    protected static final transient Logger logger= LoggerFactory.getLogger(ConnectClient.class);
    private static volatile ConcurrentMap<String,ConnectClient> connectClientMap;
    private static volatile ConcurrentMap<String,Object> connectClientLockMap=new ConcurrentHashMap<>();
    //--------------------------------------------

    public abstract void init(String  address, final Serializer serializer, final RpcInvokerFactory rpcInvokerFactory)throws Exception;

    public abstract void close();

    public abstract boolean isValidate();

    public abstract void send(RpcRequest rpcRequest)throws Exception;

    /**
     * async send
     *
     */
    public static void asyncSend(RpcRequest rpcRequest, String address, Class<? extends ConnectClient> connectClientImpl, final RpcReferenceBean rpcReferenceBean) throws Exception{
        ConnectClient connectPool=ConnectClient.getPool(address,connectClientImpl,rpcReferenceBean);

        try {
            // do invoke
            connectPool.send(rpcRequest);
        } catch (Exception e) {
            throw e;
        }
    }

    private static ConnectClient getPool(String address, Class<? extends ConnectClient> connectClientImpl, final RpcReferenceBean rpcReferenceBean) throws Exception {
        if (connectClientMap==null) {
            synchronized (ConnectClient.class){
                if (connectClientMap==null) {
                    //init
                    connectClientMap=new ConcurrentHashMap<>();

                    //stop callback

                    rpcReferenceBean.getInvokerFactory().addStopCallback(new BaseCallback() {
                        @Override
                        public void run() throws Exception {
                            if (connectClientMap.size()>0) {
                                for (String key: connectClientMap.keySet()) {
                                    ConnectClient clientPool=connectClientMap.get(key);
                                    clientPool.close();
                                }
                                connectClientMap.clear();
                            }
                        }
                    });
                }
            }
        }

        //得到存活的client

        ConnectClient connectClient=connectClientMap.get(address);

        if (connectClient != null&& connectClient.isValidate()) {
            return connectClient;
        }

        //lock
        Object clientLock=connectClientLockMap.get(address);
        if (clientLock==null) {
            connectClientLockMap.putIfAbsent(address,new Object());
            clientLock=connectClientLockMap.get(address);
        }

        //创建新的客户端删除老的client
        synchronized (clientLock){

            //get-valid client ,avlid repeat

            connectClient =connectClientMap.get(address);

            if (connectClient != null&& connectClient.isValidate()) {
                return connectClient;
            }

            //remove old
            if (connectClient != null) {
                connectClient.close();
                connectClientMap.remove(address);
            }

            //把client放进连接池

            ConnectClient connectClient1=connectClientImpl.newInstance();
            try {
                connectClient1.init(address,rpcReferenceBean.getSerializer(),rpcReferenceBean.getInvokerFactory());
                connectClientMap.put(address,connectClient1);
            } catch (Exception e) {
                connectClient1.close();
                throw e;
            }

            return connectClient1;
        }
    }
}
