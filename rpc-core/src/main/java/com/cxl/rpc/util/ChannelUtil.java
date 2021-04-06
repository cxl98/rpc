package com.cxl.rpc.util;


import io.netty.channel.Channel;

public class ChannelUtil {
    private static final ChannelUtil channels = new ChannelUtil();
    private Channel channel;

    public static ChannelUtil getChannels() {
        return channels;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void remove() {
        if (null != channel) {
            channel.close();
        }
    }
}
