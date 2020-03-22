import com.cxl.rpc.serialize.Serializer;

import java.util.HashMap;
import java.util.Map;

public class SerializerTest {
    public static void main(String[] args) {
        Serializer serializer=Serializer.SerializerEnum.JACKSON.getSerializer();
        System.out.println(serializer);
        Map<String,String> map=new HashMap<>();
        map.put("aaa", "111");
        map.put("bbb", "222");
        System.out.println();
        System.out.println(serializer.deserializer(serializer.serializer("ddddddd"), String.class));
    }
}
