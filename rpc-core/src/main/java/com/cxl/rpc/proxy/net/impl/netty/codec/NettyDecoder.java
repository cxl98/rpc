package com.cxl.rpc.proxy.net.impl.netty.codec;

import com.cxl.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author cxl
 */
public class NettyDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;
    private Serializer serializer;

    public NettyDecoder(Class<?> genericClass, final Serializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes()<4){
            return ;
        }
        in.markReaderIndex();
        int dataLength=in.readInt();
        if (dataLength<0){
            ctx.close();
        }
        if (in.readableBytes()<dataLength) {
            in.markReaderIndex();
            return ;
        }
        byte [] data=new byte[in.readableBytes()];

        in.readBytes(data);
        Object obj=serializer.deSerializer(data,genericClass);
        out.add(obj);
    }
}
