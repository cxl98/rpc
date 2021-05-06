package com.cxl.rpc.serialize;

public abstract class Serializer {
    public abstract <T> byte[] serializer(T obj);

    public abstract <T> Object deSerializer(byte[] bytes, Class<T> clazz);

}