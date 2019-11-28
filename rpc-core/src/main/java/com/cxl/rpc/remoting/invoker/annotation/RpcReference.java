package com.cxl.rpc.remoting.invoker.annotation;

import com.cxl.rpc.remoting.net.NetEnum;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcReference {
}
