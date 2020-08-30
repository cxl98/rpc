package com.cxl.rpc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class  ProxyPush implements Push{
    private Object className;
    private Object response;
    private static final ProxyPush proxypush=new ProxyPush();

    public static ProxyPush getInstance() {
        return proxypush;
    }

    public Object getClassName() {
        return className;
    }

    public void setClassName(Object className) {
        this.className = className;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    @Override
    public void exec(Object obj) {
        if (null!=obj&&!"".equals(obj)){
            response=obj;
            execInvoker();
        }
    }

    private void execInvoker() {
            try {
                Method exec = Push.class.getMethod("exec", Object.class);
                exec.invoke(className, Object.class);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
    }
}
