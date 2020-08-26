package com.cxl.rpc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum  ProxyPush {
    PUSH;
    public void execInvoke(Push push){
        try {
            Method method = push.getClass().getMethod("exec", Object.class);
            method.invoke(push,Object.class);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
