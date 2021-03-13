package com.cxl.rpc.proxy.consumer.annotation;

import com.cxl.rpc.proxy.consumer.callback.CallType;
import com.cxl.rpc.proxy.consumer.route.LoadBalance;
import com.cxl.rpc.proxy.net.NetEnum;
import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.serialize.impl.ProtostuffSerializer;

import java.lang.annotation.*;

/**
 * @author cxl
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcClient {
    NetEnum netType() default NetEnum.NETTY;

    Class<? extends Serializer> serializer() default ProtostuffSerializer.class;

    CallType callType() default CallType.SYNC;

    LoadBalance loadBalance() default LoadBalance.ROUND;


    String version() default "";

    long timeout() default 1000;

    String address() default "";

    String accessToken() default "";
}
