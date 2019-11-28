package com.cxl.rpc.util;

import java.util.HashMap;

public class ClassUtil {
    private static final HashMap<String,Class<?>> primClasses=new HashMap<>();

    static{
        primClasses.put("boolean",boolean.class);
        primClasses.put("byte",byte.class);
        primClasses.put("char",char.class);
        primClasses.put("int",int.class);
        primClasses.put("short",short.class);
        primClasses.put("double",double.class);
        primClasses.put("long",long.class);
        primClasses.put("float",float.class);
        primClasses.put("void",void.class);
    }
    public static Class<?> resolveClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            Class<?> clazz=primClasses.get(className);
            if (clazz != null) {
                return clazz;
            }else{
                throw e;
            }
        }
    }
}
