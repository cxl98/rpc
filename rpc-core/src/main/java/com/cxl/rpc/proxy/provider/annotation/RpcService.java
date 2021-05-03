package com.cxl.rpc.proxy.provider.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcService {
    /**
     * @return
     */
    String version()default "";
}
