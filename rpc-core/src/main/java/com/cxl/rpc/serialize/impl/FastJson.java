package com.cxl.rpc.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.cxl.rpc.serialize.Serializer;

import java.nio.charset.StandardCharsets;

public class FastJson  extends Serializer {
    @SuppressWarnings("unchecked")
    public <T> byte[] serializer(T obj) {
        byte[] bytes;
        bytes = JSON.toJSONBytes(obj);
        return bytes;
    }
    @SuppressWarnings("unchecked")
    public <T> Object deserializer(byte[] bytes, Class<T> clazz) {
        T obj;
        obj= JSON.parseObject(new String(bytes),clazz);
        return obj;
    }
}
