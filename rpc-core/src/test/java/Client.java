import com.cxl.rpc.remoting.consumer.annotation.RpcReference;
import com.cxl.rpc.remoting.consumer.reference.RpcReferenceBean;
import com.cxl.rpc.remoting.net.ConnectClient;
import com.cxl.rpc.remoting.net.impl.netty.client.NettyClient;
import com.cxl.rpc.remoting.net.params.RpcRequest;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public class Client {
    public static void main(String[] args) throws Exception {
//        RpcReferenceBean rpcReferenceBean=new RpcReferenceBean();
//        rpcReferenceBean.setAddress("127.0.0.1:8888");
//        rpcReferenceBean.setIface(API.class);

        SortedMap<Integer, String> mp = new TreeMap<>();

        // Adding Element to SortedSet
        mp.put(1, "One");
        mp.put(2, "Two");
        mp.put(3, "Three");
        mp.put(4, "Four");
        mp.put(5, "Five");

        // Returning the key greater
        // than or equal to 2
        System.out.print("Last Key in the map is : "
                + mp.tailMap(2));
    }
}
