import com.cxl.rpc.proxy.net.impl.netty.server.NettyServer;
import com.cxl.rpc.proxy.provider.RpcProviderFactory;

public class Server {
    public static void main(String[] args) throws Exception {
        com.cxl.rpc.proxy.net.Server server=new NettyServer();
        RpcProviderFactory rpcProviderFactory=new RpcProviderFactory();
//        rpcProviderFactory.addService(API.class);
        server.start(rpcProviderFactory);
    }
}
