package com.cxl.server.impl;

import com.cxl.api.Deom;

import com.cxl.api.dto.UserDTO;
import com.cxl.rpc.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Date;

public class DemoImpl implements Deom {
    private static final Logger LOGGER= LoggerFactory.getLogger(DemoImpl.class);

    @Override
    public Object say(String name, String password) {
        UserDTO user=new UserDTO(name,password);
        LOGGER.info(user.toString());
        return user;
    }
}
