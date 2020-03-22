package com.cxl.rpc.remoting.invoker.annotation;

import com.cxl.rpc.remoting.invoker.call.CallType;
import com.cxl.rpc.remoting.invoker.route.LoadBalance;
import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.serialize.Serializer;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcReference {
    NetEnum netType() default NetEnum.NETTY;

    Serializer.SerializerEnum serializer() default Serializer.SerializerEnum.JACKSON;

    CallType callType() default CallType.SYNC;

    LoadBalance loadBalance() default LoadBalance.ROUND;


    String version() default "";

    long timeout() default 1000;

    String address() default "";

    String accessToken() default "";
}
