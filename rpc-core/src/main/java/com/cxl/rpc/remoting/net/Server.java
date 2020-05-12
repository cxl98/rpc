package com.cxl.rpc.remoting.net;

import com.cxl.rpc.remoting.net.params.BaseCallback;
import com.cxl.rpc.remoting.provider.RpcProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Server {
    protected static final Logger LOGGER= LoggerFactory.getLogger(Server.class);

    private BaseCallback startedCallback;
    private BaseCallback stopCallback;

//    public BaseCallback getStartedCallback() {
//        return startedCallback;
//    }

    public void setStartedCallback(BaseCallback startedCallback) {
        this.startedCallback = startedCallback;
    }

//    public BaseCallback getStopCallback() {
//        return stopCallback;
//    }

    public void setStopCallback(BaseCallback stopCallback) {
        this.stopCallback = stopCallback;
    }

    /**
     * start server
     */
    public abstract void start(final RpcProviderFactory rpcProviderFactory)throws Exception;

    /**
     * callback when started
     */
    public void onStarted(){
        if (startedCallback != null) {
            try {
                startedCallback.run();
            } catch (Exception e) {
                LOGGER.error(">>>>>>>>>>rpc, server startedCallback error.",e);
            }
        }
    }

    /**
     * stop server
     */
    public abstract void stop() throws Exception;

    /**
     * callback when stop
     */
    public void onStop(){
        if (stopCallback != null) {
            try {
                stopCallback.run();
            } catch (Exception e) {
                LOGGER.error(">>>>>>>>>rpc, server StopCallback error .",e);
            }
        }
    }
}
