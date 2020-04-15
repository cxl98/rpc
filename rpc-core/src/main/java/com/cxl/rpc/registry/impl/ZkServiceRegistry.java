package com.cxl.rpc.registry.impl;

import com.cxl.rpc.registry.ServiceRegistry;
import com.cxl.rpc.util.RpcException;
import com.cxl.rpc.util.ZkClientUtil;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ZkServiceRegistry extends ServiceRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceRegistry.class);

    public static final String ENV = "env";
    public static final String ZK_ADDRESS = "address";

    private String zkEnvPath;
    private ZkClientUtil zkClient = null;

    private Thread refreshThread;
    private volatile boolean refreshThreadStop = false;

    private ConcurrentMap<String, TreeSet<String>> registryData = new ConcurrentHashMap<>();
    private ConcurrentMap<String, TreeSet<String>> discoveryData = new ConcurrentHashMap();

    private String keyToPath(String nodeKey) {
        return "/" + nodeKey;
    }

    private String pathToKey(String nodePath) {
        if (null == nodePath || zkEnvPath.length() >= nodePath.length()) {
            return null;
        }
        return nodePath.substring(zkEnvPath.length() + 1);
    }

    @Override
    public void start(Map<String, String> param) {
        String address = param.get(ZK_ADDRESS);
        String env = param.get(ENV);

        if (null == address || 0 == address.length()) {
            throw new RpcException("rpc zkAddress can't be empty");
        }
        if (null == env || 0 == env.length()) {
            throw new RpcException("rpc env can't be empty");
        }
        zkEnvPath = keyToPath(env);

        zkClient = new ZkClientUtil(address, zkEnvPath, watchedEvent -> {
            LOGGER.debug(">>>>>>>>>>>>  rpc: watcher:{}", watchedEvent);
            if (Watcher.Event.KeeperState.Expired == watchedEvent.getState()) {
                zkClient.destroy();
                zkClient.getClient();

                //到期重试
                refreshDiscoveryData(null);

                LOGGER.debug(">>>>>>>>> rpc,zk 重新连接　重新加载成功");
            }

            //watch + refresh
            String path = watchedEvent.getPath();

            System.out.println("路径：" + path);

            String key = pathToKey(path);

            System.out.println("key: " + key);

            if (null != key) {
                //保持监视 key:添加一次性触发器
                try {
                    zkClient.getClient().exists(path, true);

                    //刷新
                    if (watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                        //刷新发现数据(单个数据)：一个一个改变
                        refreshDiscoveryData(key);
                    }
                } catch (KeeperException | InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

        });

        zkClient.getClient();

        refreshThread=new Thread(()->{
           while(!refreshThreadStop){
               try {
                   TimeUnit.SECONDS.sleep(30);

                    //定期检查
                   refreshDiscoveryData(null);

                   refreshRegistryData();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
        });
    }

    private void refreshRegistryData() {
        if (0<registryData.size()){
            for (Map.Entry<String,TreeSet<String>> item: registryData.entrySet()) {
                String key=item.getKey();
                for (String value: item.getValue()) {
                    String path=keyToPath(key);
                    zkClient.setChildPathData(path,value,"");
                }
            }
            LOGGER.info(">>>>>>>>>>> rpc, 刷新注册中心成功, registryData = {}", registryData);
        }

    }

    /**
     * 刷新发现数据和缓存
     *
     * @param key 服务id
     */
    private void refreshDiscoveryData(String key) {
        Set<String> keys = new HashSet<>();
        if (null != key && 0 < key.length()) {
            keys.add(key);
        } else {
            if (0 < discoveryData.size()) {
                keys.addAll(discoveryData.keySet());
            }
        }
        if (0 < keys.size()) {
            for (String item : keys) {
                //增加values
                String path = keyToPath(item);
                Map<String, String> childPathData = zkClient.getChildPathData(path);

                //是否存在values
                TreeSet<String> existValues = discoveryData.computeIfAbsent(item, k -> new TreeSet<>());
                if (0 < childPathData.size()) {
                    existValues.clear();
                    existValues.addAll(childPathData.keySet());
                }
            }
            LOGGER.info(">>>>>>>>>>  rpc,刷新发现数据成功, 发现数据={}", discoveryData);
        }
    }

    @Override
    public void stop() {
        if (null != zkClient) {
            zkClient.destroy();
        }
        if (null != refreshThread && refreshThread.isInterrupted()) {
            refreshThreadStop = true;
            refreshThread.interrupt();
        }
    }

    @Override
    public boolean registry(Set<String> keys, String value) {
        for (String key : keys) {
            //本地缓存
            TreeSet<String> values = registryData.computeIfAbsent(key, k -> new TreeSet<>());
            values.add(value);

            String path = keyToPath(key);
            zkClient.setChildPathData(path, value, "");
        }
        LOGGER.info(">>>>>>>>>>.rpc, 注册成功, keys={},value={}", keys, value);
        return true;
    }

    @Override
    public boolean remove(Set<String> keys, String value) {
        for (String key : keys) {
            TreeSet<String> values = discoveryData.get(key);
            if (null != values) {
                values.remove(value);
            }
            String path = keyToPath(key);
            zkClient.deleteChildPath(path, value);
        }
        LOGGER.info(">>>>>>>>>>>rpc,删除成功,  keys={},value={}", keys, value);
        return true;
    }

    @Override
    public Map<String, TreeSet<String>> discovery(Set<String> keys) {
        if (null == keys || 0 == keys.size()){
            return null;
        }
        Map<String,TreeSet<String>> tempData=new HashMap<>();
        for (String key: keys) {
            TreeSet<String> value=discovery(key);
            if (null!=value){
                tempData.put(key,value);
            }
        }
        return tempData;
    }

    @Override
    public TreeSet<String> discovery(String key) {
        //本地缓存
        TreeSet<String> values=discoveryData.get(key);
        if (null==values){
            refreshDiscoveryData(key);
            values=discoveryData.get(key);
        }
        return values;
    }
}
