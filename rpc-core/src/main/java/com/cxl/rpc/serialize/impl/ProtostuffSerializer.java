package com.cxl.rpc.serialize.impl;

import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.RpcException;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffSerializer extends Serializer {
    private static final Objenesis OBJENESIS=new ObjenesisStd(true);

    private static Map<Class<?>, Schema<?>> cachedSchema= new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clazz){
        return (Schema<T>) cachedSchema.computeIfAbsent(clazz,RuntimeSchema::createFrom);
    }
    @SuppressWarnings("unchecked")
    public <T> byte[] serializer(T obj) {

        Class<T> clazz= (Class<T>) obj.getClass();

        LinkedBuffer buffer=LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        try {
            Schema<T> schema=getSchema(clazz);

            return ProtostuffIOUtil.toByteArray(obj,schema,buffer);
        } catch (Exception e) {
            throw new RpcException(e);
        }finally {
            buffer.clear();
        }
    }

    public <T> Object deserializer(byte[] bytes, Class<T> clazz) {
        try {
            T message=OBJENESIS.newInstance(clazz);
            Schema<T> schema=getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(bytes,message,schema);
            return message;
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }
}
