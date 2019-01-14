package com.sxt.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lenovo on 2019/1/14.
 */
public class ZKBase implements Watcher{
    private ZooKeeper zooKeeper = null;

    public  static final String CONNECT_ADDR = "192.168.41.129:2181";
    public static final Integer SESSION_OUTTIME = 5000;

    public static CountDownLatch countDown = new CountDownLatch(1);
    public ZKBase() {
    }

    public ZKBase(String connect) {
        try {
            zooKeeper = new ZooKeeper(connect,SESSION_OUTTIME,new ZKBase());
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

    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.KeeperState state = watchedEvent.getState();
        Event.EventType eventType = watchedEvent.getType();
        if(Event.KeeperState.SyncConnected == state) {
            if(Event.EventType.None == eventType) {
                //如果建立连接成功，则发送信号量
                countDown.countDown();
                System.out.println("zk建立连接");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ZKBase zkBase = new ZKBase(CONNECT_ADDR);
        countDown.await();
    }
}
