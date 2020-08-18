import com.cxl.rpc.remoting.net.impl.netty.server.NettyServer;
import com.cxl.rpc.remoting.provider.RpcProviderFactory;

import java.util.concurrent.TimeUnit;

public class Server {
    public static void main(String[] args) throws Exception {
        com.cxl.rpc.remoting.net.Server server=new NettyServer();
        RpcProviderFactory rpcProviderFactory=new RpcProviderFactory();
//        rpcProviderFactory.addService(API.class);
        server.start(rpcProviderFactory);
    }
}
