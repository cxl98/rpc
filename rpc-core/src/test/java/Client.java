import com.cxl.rpc.remoting.consumer.annotation.RpcReference;
import com.cxl.rpc.remoting.consumer.reference.RpcReferenceBean;
import com.cxl.rpc.remoting.net.ConnectClient;
import com.cxl.rpc.remoting.net.impl.netty.client.NettyClient;
import com.cxl.rpc.remoting.net.params.RpcRequest;

import java.util.UUID;

public class Client {
    public static void main(String[] args) throws Exception {
        RpcReferenceBean rpcReferenceBean=new RpcReferenceBean();
        rpcReferenceBean.setAddress("127.0.0.1:8888");
        rpcReferenceBean.setIface(API.class);
    }
}
