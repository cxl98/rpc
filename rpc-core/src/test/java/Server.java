import com.cxl.rpc.remoting.net.NetEnum;
import com.cxl.rpc.remoting.net.impl.netty.server.NettyServer;
import com.cxl.rpc.remoting.provider.RpcProviderFactory;
import com.cxl.rpc.serialize.Serializer;

import java.util.concurrent.TimeUnit;

public class Server {
    public static void main(String[] args) throws Exception {
        com.cxl.rpc.remoting.net.Server server=new NettyServer();
        RpcProviderFactory rpcProviderFactory=new RpcProviderFactory();
        server.start(rpcProviderFactory);
        while(!Thread.currentThread().isInterrupted()){
            TimeUnit.HOURS.sleep(1);
        }
        server.stop();
    }
}
