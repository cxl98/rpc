package com.cxl.rpc.serialize.impl;

import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.RpcException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class JacksonSerializer extends Serializer {
    private static final ObjectMapper objMapper = new ObjectMapper();

    static {
        objMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        objMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * bean、array、List、Map --> json
     *
     * @param obj
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> byte[] serializer(T obj) {

        byte[] bytes;
        try {
            bytes = objMapper.writeValueAsBytes(obj);

        } catch (JsonProcessingException e) {
            throw new RpcException(e);
        }
        return bytes;
    }

    /**
     * string --> bean、Map、List(array)
     *
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> Object deserializer(byte[] bytes, Class<T> clazz) {
        T obj = null;
        try {
            obj = objMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
