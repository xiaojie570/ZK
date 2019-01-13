package com.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.EventType;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lenovo on 2019/1/13.
 */
public class ZKGetChildrenList implements Watcher {
    private ZooKeeper zooKeeper = null;

    public static final String zkServerPath = "192.168.41.129:2181";
    public static final Integer timeout = 5000;

    public static CountDownLatch countDownLatch = new CountDownLatch(1);
    public ZKGetChildrenList() {
    }

    public ZKGetChildrenList(String connectString) {
        try {
            zooKeeper = new ZooKeeper(connectString,timeout,new ZKGetChildrenList());
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
        ZKGetChildrenList zkServer = new ZKGetChildrenList(zkServerPath);

        /**
         * path：父节点路径
         * watch：true或者false，注册一个watch事件
         */
        /*List<String> strChildList = zkServer.getZooKeeper().getChildren("/test",true);
        for(String s : strChildList) {
            System.out.println(s);
        }*/

        // 异步调用
        String ctx = "{'callback':'ChildrenCallback'}";
        zkServer.getZooKeeper().getChildren("/test", true, new AsyncCallback.ChildrenCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, List<String> childrenlist) {
                for(String s: childrenlist) {
                    System.out.println(s);
                }

                System.out.println("ChildrenCallback:" + path);
                System.out.println((String)ctx);
            }
        },ctx);

        countDownLatch.await();
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
            System.out.println("NodeChildrenChanged");
            ZKGetChildrenList zkServer = new ZKGetChildrenList(zkServerPath);
            try {
                List<String> strChildList = zkServer.getZooKeeper().getChildren(watchedEvent.getPath(),false);
                for(String s : strChildList) {
                    System.out.println(s);
                }
                countDownLatch.countDown();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }
}
