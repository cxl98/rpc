import com.cxl.rpc.remoting.net.params.RpcRequest;
import com.cxl.rpc.remoting.net.params.RpcResponse;
import com.cxl.rpc.serialize.Serializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SerializerTest {
    public static void main(String[] args) {
        Serializer serializer=Serializer.SerializerEnum.GSON.getSerializer();
        RpcRequest request=new RpcRequest();
        request.setRequestId("111");
        request.setCreateMillisTime(System.currentTimeMillis());
        request.setAccessToken(null);
        request.setClassName("com.cxl.cxl");
        request.setMethodName("say");
        request.setParameterTypes(String.class.getClasses());
        request.setParameters(new String[]{"陈新林"});

        System.out.println(Arrays.toString(serializer.serializer(request)));
        RpcResponse response=new RpcResponse();
        System.out.println(serializer.deserializer(serializer.serializer(request),response.getClass()));


    }
}
