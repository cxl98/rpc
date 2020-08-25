package com.cxl.server.impl;

import com.cxl.api.Deom;
import com.cxl.api.dto.UserDTO;
import com.cxl.rpc.remoting.net.params.RpcResponse;
import com.cxl.rpc.util.ChannelUtil;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DemoImpl implements Deom {
    private static final Logger LOGGER= LoggerFactory.getLogger(DemoImpl.class);
    private List<Channel> list=new CopyOnWriteArrayList<>();
    @Override
    public Object say(String name, String password) throws InterruptedException {
        UserDTO user=new UserDTO(name,password);
        Channel channel = ChannelUtil.getChannels().getChannel();
        list.add(channel);
        for (Channel item: list) {
            if (channel!=item){
                RpcResponse rpcResponse=new RpcResponse();
                rpcResponse.setRequestId(user.getName());
                rpcResponse.setResult(user);
                item.writeAndFlush(rpcResponse).sync();
            }
        }
        System.out.println("xxxx");
      return user;
    }
}
