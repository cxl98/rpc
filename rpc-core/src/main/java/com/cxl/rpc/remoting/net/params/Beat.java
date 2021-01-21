package com.cxl.rpc.remoting.net.params;

public class Beat {
    public static final int BEAT_INTERVAL = 30;
    public static final String BEAT_ID="BEAT_PING_PONG";
    public static final RpcRequest BEAT_PING;
    static{
        BEAT_PING=new RpcRequest(){};
        BEAT_PING.setRequestId(BEAT_ID);
    }
}
