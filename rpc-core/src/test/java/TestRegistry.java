import com.cxl.rpc.registry.ServiceRegistry;
import com.cxl.rpc.registry.impl.RegistryServiceRegistry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestRegistry {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, InterruptedException {
        Map<String,String> param=new HashMap<>();
        param.put(RegistryServiceRegistry.REGISTRY_ADDRESS,"http://localhost:8000/registry-admin");
        param.put(RegistryServiceRegistry.ENV,"test");

        Class<? extends ServiceRegistry> servicerRegistryClass=RegistryServiceRegistry.class;

        ServiceRegistry serviceRegistry=servicerRegistryClass.newInstance();
        serviceRegistry.start(param);

        String serviceName="demo";
        System.out.println(serviceRegistry.discovery(serviceName));

        serviceRegistry.registry(new HashSet<>(Arrays.asList(serviceName)),"127.0.0.1:8889");
        TimeUnit.MILLISECONDS.sleep(10);

        System.out.println(serviceRegistry.discovery(serviceName));

        serviceRegistry.registry(new HashSet<>(Arrays.asList(serviceName)),"127.0.0.1:9990");
        TimeUnit.MILLISECONDS.sleep(10);

        System.out.println(serviceRegistry.discovery(serviceName));
    }
}
