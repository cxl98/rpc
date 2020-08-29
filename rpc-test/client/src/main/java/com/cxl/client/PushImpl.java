package com.cxl.client;

import com.cxl.rpc.util.Push;

public class PushImpl implements Push {
    @Override
    public void exec(Object obj) {
        System.out.println("Push"+obj);
    }
}
