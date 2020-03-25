import com.cxl.rpc.serialize.Serializer;
import com.cxl.rpc.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SerializerTest {
    public static void main(String[] args) {
        Serializer serializer=Serializer.SerializerEnum.PROTOSTUFF.getSerializer();
        System.out.println(serializer);
        Map<String,String> map=new HashMap<>();
        map.put("aaa", "111");
        map.put("bbb", "222");
        System.out.println();
        System.out.println(serializer.deserializer(serializer.serializer("dddd"), String.class));

    }
}
