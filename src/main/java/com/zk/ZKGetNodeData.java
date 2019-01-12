package com.zk;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lenovo on 2019/1/12.
 */
public class ZKGetNodeData implements Watcher{

    private ZooKeeper zooKeeper = null;

    public static final String zkServerPath = "192.168.41.129:2181";
    public static final Integer timeout = 5000;
    private static Stat stat = new Stat();
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    public ZKGetNodeData() {
    }

    public ZKGetNodeData(String connectString) {
        try {
            zooKeeper = new ZooKeeper(connectString,timeout,new ZKGetNodeData());
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
        ZKGetNodeData zkGetNodeData = new ZKGetNodeData(zkServerPath);

        byte[] restByte = zkGetNodeData.getZooKeeper().getData("/test",true,stat);
        String result = new String(restByte);
        System.out.println("当前值为：" + result);
        countDownLatch.await();
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            if(event.getType() == Event.EventType.NodeDataChanged) {
                ZKGetNodeData zkGetNodeData = new ZKGetNodeData(zkServerPath);
                byte[] restByte = zkGetNodeData.getZooKeeper().getData("/test",false,stat);
                String result = new String(restByte);
                System.out.println("更改后的值：" + result);
                System.out.println("版本号变化dversion：" + stat.getVersion());
                countDownLatch.countDown();
            } else if(event.getType() == Event.EventType.NodeCreated) {

            } else if(event.getType() == Event.EventType.NodeDeleted) {

            } else if(event.getType() == Event.EventType.NodeChildrenChanged) {

            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }
}
