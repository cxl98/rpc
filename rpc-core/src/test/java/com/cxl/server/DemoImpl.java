package com.cxl.server;

import com.cxl.api.Demo;
import com.cxl.api.User;
import com.cxl.rpc.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Date;

public class DemoImpl implements Demo {
    private static Logger logger= LoggerFactory.getLogger(DemoImpl.class);


    @Override
    public User say(String name) {
        String word= MessageFormat.format("你号 {0}, 这是来自:  {1}  在 {2}",name,DemoImpl.class.getName(), DateUtil.format(new Date(),DateUtil.getDatetimeFormat()));
        User user=new User(name,word);
        return user;
    }
}
