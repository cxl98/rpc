package com.cxl.rpc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class  ProxyPush implements Push{
    private Object className;
    private Push push;
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

    @Override
    public void exec(Object obj) {
        if (null!=obj&&!"".equals(obj)){
            execInvoker();
        }
    }

    private void execInvoker() {
        if (null == push){
            try {
                Method exec = Push.class.getMethod("exec", Object.class);
                exec.invoke(className,Object.class);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
