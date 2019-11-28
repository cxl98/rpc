package com.cxl.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class NetUtil {
    private static final Logger LOGGER= LoggerFactory.getLogger(NetUtil.class);
    /**
     * find avaliable port
     *
     * @param defaultPort
     * @return
     */
    public static int findAvailablePort(int defaultPort){
        int portTmp=defaultPort;
        while(portTmp<65535){
          if (!isPortUsed(portTmp)){
              return portTmp;
          }else{
              portTmp++;
          }
        }
        portTmp=defaultPort--;
        while(portTmp>0){
            if (!isPortUsed(portTmp)) {
                return portTmp;
            }else{
                portTmp++;
            }
        }
        throw new RpcException("no available port");
    }

    /**
     * check port used
     *
     * @param port
     * @return
     */
    private static boolean isPortUsed(int port) {
        boolean used=false;
        ServerSocket serverSocket=null;

        try {
            serverSocket=new ServerSocket(port);
            used=false;
        } catch (IOException e) {
          LOGGER.info(">>>>>>>>>>>rpc,port[{}] is in use.",port);
          used=true;
        }finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    LOGGER.info("");
                }
            }
        }
        return used;
    }
}
