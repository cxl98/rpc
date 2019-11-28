package com.cxl.rpc.serialize.impl;

import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.RpcException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JacksonSerializer extends Serializer {
    private static final ObjectMapper OBJECT_MAPPER=new ObjectMapper();

    /**
     *  bean、array、List、Map --> json
     * @param obj
     * @param <T>
     * @return
     */
    public <T> byte[] serializer(T obj) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RpcException(e);
        }
    }

    /**
     * string --> bean、Map、List(array)
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Object deserializer(byte[] bytes, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(bytes,clazz);
        } catch (IOException e) {
            throw new RpcException(e);
        }
    }
}
