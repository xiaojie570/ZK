package com.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;

import java.io.IOException;
import java.util.List;

/**
 * Created by lenovo on 2019/1/12.
 */
public class ZKNodeOperator implements Watcher {
    ZooKeeper zooKeeper = null;
    public static final String zkServerPath = "192.168.41.129:2181";
    public static final Integer timeout = 5000;

    public ZKNodeOperator() {
    }

    public ZKNodeOperator(String connectString) {
        try {
            zooKeeper = new ZooKeeper(connectString,timeout,new ZKNodeOperator());
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


    /**
     * 创建ZK节点
     * @param path  创建的路径
     * @param data  存储的数据的byte[]
     * @param acls  控制权限策略
     */
    public void createZKNode(String path, byte[] data, List<ACL> acls) {
        String result = "";
        try {
            /**
             * 同步或者异步创建节点，都不支持子节点的递归创建，异步有一个callback函数
             * 参数：
             * path：创建的路径
             * data：存储的数据的 byte[]
             * acl：控制权限策略
             * createMode：节点类型，是一个枚举
             *          PERSISTENT：持久节点
             *          EPHEMERAL：临时节点
             */
            // 使用同步的方式来创建节点
            // 创建一个临时节点
            //result = zooKeeper.create(path,data,acls, CreateMode.EPHEMERAL);

            String ctx = "{'create':'success'}";
            // 使用异步的方式来创建节点
            zooKeeper.create(path,data,acls,CreateMode.PERSISTENT,new CreateCallBack(),ctx);
            System.out.println("创建节点： \t" +  result + "\t 成功");
            new Thread().sleep(2000);

        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        ZKNodeOperator zkNodeOperator = new ZKNodeOperator(zkServerPath);

        zkNodeOperator.createZKNode("/test-delete-node1","123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE);

        String ctx = "{'delete':'test-delete-node1'}";
        zkNodeOperator.getZooKeeper().delete("/test-delete-node1",0,new DeleteCallBack(),ctx);
        Thread.sleep(2000);
    }
    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
