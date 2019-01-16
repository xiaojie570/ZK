package com.sxt.zk;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lenovo on 2019/1/16.
 */
public class ZKWatcher implements Watcher {
    // 定义原子变量
    AtomicInteger seq = new AtomicInteger();
    // 定义session失效时间
    private static final int SESSION_TIMEOUT = 5000;
    // ZK 服务器地址
    private static final String CONNECTION_ADDR = "192.168.41.129:2181";
    // ZK 父路径
    private static final String PARENT_PATH = "/testfj";
    // ZK 子路径设置
    private static final String CHILDREN_PATH = "/testfj/children";
    // 进入标识
    private static final String LOG_PREFIX_OF_MAIN="【Main】";
    // ZK 变量
    private ZooKeeper zooKeeper = null;
    // 信号量设置，用于等待 ZK 连接建立之后，通知阻塞程序继续向下执行
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * 创建 ZK 连接
     * @param connectAddr ZK 服务器地址
     * @param sessionTime Seesion 超时时间
     */
    public void createConnection(String connectAddr, int sessionTime) {
        this.releaseConnection();
        try {
            zooKeeper = new ZooKeeper(connectAddr,sessionTime,this);
            System.out.println(LOG_PREFIX_OF_MAIN + "开始连接 ZK 服务器");
            countDownLatch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭 ZK 连接
     */
    public void releaseConnection() {
        if(this.zooKeeper != null)
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    /**
     * 创建节点
     * @param path 节点路径
     * @param data 数据内容
     * @return
     */
    public boolean createPath(String path, String data) {

        try {
            // 设置监控（由于 ZK 的监控都是一次性的，所以每次必须设置监控）
            zooKeeper.exists(path,true);
            System.out.println(LOG_PREFIX_OF_MAIN + "节点创建成功，Path : " +
                    this.zooKeeper.create(path,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT) +
                    ", content:" + data);

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 读取指定节点数据内容
     * @param path  节点路径
     * @param needWatch  是否有watch
     * @return
     */
    public String readData(String path,boolean needWatch) {
        try {
            return new String(zooKeeper.getData(path,needWatch,null));
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 更新指定节点数据内容
     * @param path 节点路径
     * @param data 数据内容
     * @return
     */
    public boolean upData(String path, String data) {
        try {
            System.out.println(LOG_PREFIX_OF_MAIN + "更新数据成功， path: " + path + ", stat:" + zooKeeper.setData(path,data.getBytes(), -1));
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
