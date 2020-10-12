package com.cxl.rpc.serialize.impl;

import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.RpcException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSerializer extends Serializer {

    private Gson gson=new Gson();
    @SuppressWarnings("unchecked")
    @Override
    public <T> byte[] serializer(T obj) {
        if (null==obj){
            throw new RpcException("Invalid obj");
        }
        String s = gson.toJson(obj);
        return s.getBytes();
    }

    @Override
    public <T> Object deserializer(byte[] bytes, Class<T> clazz) {
        T t = gson.fromJson(new String(bytes), clazz);
        return t;
    }
}
