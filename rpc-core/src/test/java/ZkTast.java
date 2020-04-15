import com.cxl.rpc.util.ZkClientUtil;

import java.util.concurrent.TimeUnit;

public class ZkTast {
    public static void main(String[] args) throws InterruptedException {
        ZkClientUtil client;

        client=new ZkClientUtil("127.0.0.1:2181","/rpc/test",null);
        String path="/rpc/test";
        for (int i = 0; i <2 ; i++) {
            System.out.println("-----------"+i);
            System.out.println(client.getClient());

            TimeUnit.SECONDS.sleep(1);
        }
        client.setPathData(path,"127.0.0.1:8888",false);
        String pathData = client.getPathData(path, false);

        System.out.println(pathData);
        client.deletePath(path,false);
    }
}
