package com.cxl.rpc.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.cxl.rpc.serialize.Serializer;

public class FastJson  extends Serializer {
    @Override
    public <T> byte[] serializer(T obj) {
        byte[] bytes;
        bytes = JSON.toJSONBytes(obj);
        return bytes;
    }

    @Override
    public <T> Object deserializer(byte[] bytes, Class<T> clazz) {
        T obj;
        obj=JSON.parseObject(bytes,clazz);
        return obj;
    }
}
