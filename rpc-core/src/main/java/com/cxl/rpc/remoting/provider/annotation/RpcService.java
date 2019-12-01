package com.cxl.rpc.remoting.provider.annotation;

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
