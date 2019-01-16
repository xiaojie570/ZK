package com.sxt.zk;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
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

    public static void main(String[] args) throws InterruptedException, KeeperException {
        ZKBase zkBase = new ZKBase(CONNECT_ADDR);
        countDown.await();
        //String ret = zkBase.getZooKeeper().create("/aaaaaa","lianhuajie".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        //System.out.println("返回值： " + ret);

        List<String > list = zkBase.getZooKeeper().getChildren("/test",false);

        for(String path : list) {
            System.out.println(path);
            String realPath = "/test/" + path;
            System.out.println(new String (zkBase.getZooKeeper().getData(realPath,false,null)));
        }
        // 163208757255,163208757255,1547633550420,1547633550420,0,0,0,0,2,0,163208757255
        System.out.println(zkBase.getZooKeeper().exists("/fj",false));
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }
}
