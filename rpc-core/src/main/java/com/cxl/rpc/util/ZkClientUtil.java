package com.cxl.rpc.util;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ZkClientUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkClientUtil.class);

    private String zkadress;
    private String zkpath;
    private Watcher watcher;
    private ZooKeeper zooKeeper;
    private CountDownLatch countDownLatch = new CountDownLatch(1);


    public ZkClientUtil(String zkadress, String zkpath, Watcher watcher) {
        this.zkadress = zkadress;
        this.zkpath = zkpath;
        this.watcher = watcher;
        if (null == this.watcher) {
            this.watcher = watchedEvent -> {
                LOGGER.info(">>>>>>rpc:watcher:{}", watchedEvent);
                //session 过时（失效）,关闭旧的连接，创建新的连接
                if (watchedEvent.getState() == Watcher.Event.KeeperState.Expired) {
                    destroy();
                    getClient();
                }
            };
        }
    }

    public ZooKeeper getClient() {
        if (null == zooKeeper) {
            try {
                ZooKeeper newZk ;
                newZk = new ZooKeeper(zkadress, 5000, watcher = watchedEvent -> countDownLatch.countDown());
                newZk.exists(zkpath,false);
                zooKeeper=newZk;
                countDownLatch.await();
                LOGGER.info("rpc,ZkClient init success");
            } catch (IOException | InterruptedException | KeeperException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return zooKeeper;
    }

    public void destroy() {
        if (null != zooKeeper) {
            try {
                zooKeeper.close();
                zooKeeper = null;
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void createPath(String path, boolean watch) {
        try {
            Stat stat = getClient().exists(path, watch);
            if (null == stat) {
                if (0 < path.lastIndexOf("/")) {
                    String parentPath = path.substring(0, path.lastIndexOf("/"));
                    Stat parentStat = getClient().exists(parentPath, watch);
                    if (null == parentStat) {
                        createPath(parentPath, false);
                    }
                }
                getClient().create(path, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            getClient().exists(path, true);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void deletePath(String path, boolean watch) {
        try {
            Stat stat = getClient().exists(path, watch);
            if (null != stat) {
                getClient().delete(path, stat.getVersion());
            } else {
                LOGGER.info(">>>>>zookeeper 节点路径找不到:{}", path);
            }
        } catch (KeeperException | InterruptedException e) {
            throw new RpcException(e);
        }
    }

    /**
     * 给该路径的节点设置data
     * @param path zookeeper的路径
     * @param data　该路径的数据
     */
    public Stat setPathData(String path,String data,boolean watch){
        try {
            Stat stat=getClient().exists(path,watch);
            if (null==stat){
                createPath(path,watch);
                stat=getClient().exists(path,watch);
            }
            return getClient().setData(path,data.getBytes(StandardCharsets.UTF_8),stat.getVersion());
        } catch (KeeperException | InterruptedException e) {
            throw new RpcException(e);
        }
    }
    public String getPathData(String path, boolean watch) {
        String znode = null;
        try {
            Stat stat = getClient().exists(path, watch);
            if (null != stat) {
                byte[] resultData = getClient().getData(path, watch, null);
                if (null != resultData) {
                    znode = new String(resultData, StandardCharsets.UTF_8);
                } else {
                    LOGGER.info(">>>>>rpc,path{} not found", path);
                }
            }
            return znode;
        } catch (KeeperException | InterruptedException e) {
            throw new RpcException(e);
        }
    }

    public void setChildPathData(String path, String childNode, String childNodeData) {
        try {
            //创建路径
            createPath(path, false);

            //创建子路径
            String child = path.concat("/").concat(childNode);
            Stat stat = getClient().exists(child, false);
            if (null != stat) {
                if (0 == stat.getEphemeralOwner()) {
                    getClient().delete(child, stat.getVersion());
                } else {
                    return;
                }
            }
            getClient().create(child, childNodeData.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    public void deleteChildPath(String path, String childNode) {
        try {
            String child = path.concat("/").concat(childNode);
            deletePath(child, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Map<String, String> getChildPathData(String path) {
        Map<String, String> data = new HashMap<>();

        try {
            Stat stat = getClient().exists(path, false);
            if (null == stat) {
                return data;
            } else {
                List<String> childNodes = getClient().getChildren(path, true);
                if (null != childNodes && 0 < childNodes.size()) {
                    for (String childNode : childNodes) {

                        String childData = path.concat("/").concat(childNode);
                        String value=getPathData(childData,false);
                        data.put(childNode,value);
                    }
                }
                return data;
            }
        } catch (KeeperException | InterruptedException e) {
            throw new RpcException(e);
        }
    }

}
