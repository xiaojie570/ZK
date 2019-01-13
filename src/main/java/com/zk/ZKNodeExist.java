package com.zk;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lenovo on 2019/1/13.
 */
public class ZKNodeExist implements Watcher {
    private ZooKeeper zooKeeper = null;

    public static final String serverPath = "192.168.41.129:2181";
    public static final Integer timeout = 5000;

    public static CountDownLatch countDownLatch = new CountDownLatch(1);
    public ZKNodeExist() {
    }

    public ZKNodeExist(String connectPath) {
        try {
            this.zooKeeper = new ZooKeeper(connectPath,timeout,new ZKNodeExist());
        } catch (IOException e) {
            e.printStackTrace();
            if(zooKeeper != null)
                try {
                    zooKeeper.close();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
        }
    }


    public static void main(String[] args) throws KeeperException, InterruptedException {
        ZKNodeExist zkNodeExist = new ZKNodeExist(serverPath);

        Stat stat = zkNodeExist.getZooKeeper().exists("/test",true);
        if(stat != null) {
            System.out.println("该节点存在，节点的版本号为：" + stat.getVersion());
        }else {
            System.out.println("该节点不存在");
        }

        countDownLatch.await();

    }
    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType() == Event.EventType.NodeCreated) {
            System.out.println("创建该节点。。。 ");
            countDownLatch.countDown();
        } else if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
            System.out.println("该节点的子节点改变");
            countDownLatch.countDown();
        } else if(watchedEvent.getType() == Event.EventType.NodeDeleted) {
            System.out.println("删除该节点。。。");
            countDownLatch.countDown();
        } else if(watchedEvent.getType() == Event.EventType.NodeDataChanged) {
            System.out.println("该节点的数据改变");
            countDownLatch.countDown();
        }
    }
}
