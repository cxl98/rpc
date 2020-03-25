package com.cxl.rpc.serialize.impl;

import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.RpcException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class JacksonSerializer extends Serializer {
    private static final ObjectMapper objMapper = new ObjectMapper();

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        objMapper.setDateFormat(dateFormat);
        objMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        objMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, false);
        objMapper.disable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        objMapper.disable(SerializationFeature.CLOSE_CLOSEABLE);
        objMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
    }

    /**
     * bean、array、List、Map --> json
     *
     * @param obj
     * @param <T>
     * @return
     */
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
