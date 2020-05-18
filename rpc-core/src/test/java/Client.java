import com.cxl.rpc.remoting.net.impl.netty.client.NettyClient;
import com.cxl.rpc.remoting.net.params.RpcRequest;

import java.util.UUID;

public class Client {
    public static void main(String[] args) throws Exception {
        com.cxl.rpc.remoting.net.Client client=new NettyClient();
        RpcRequest request=new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setMethodName("xx");
        client.asyncSend("127.0.0.1",request);
    }
}
