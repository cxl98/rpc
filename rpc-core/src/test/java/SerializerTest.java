import com.cxl.rpc.proxy.net.params.RpcRequest;
import com.cxl.rpc.proxy.net.params.RpcResponse;
import com.cxl.rpc.serialize.Serializer;

import java.util.Arrays;

public class SerializerTest {
    public static void main(String[] args) {

        RpcRequest request=new RpcRequest();
        request.setRequestId("111");
        request.setCreateMillisTime(System.currentTimeMillis());
        request.setAccessToken(null);
        request.setClassName("com.cxl.cxl");
        request.setMethodName("say");
        request.setParameterTypes(String.class.getClasses());
        request.setParameters(new String[]{"陈新林"});


    }
}
