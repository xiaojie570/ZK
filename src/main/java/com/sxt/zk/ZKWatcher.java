package com.sxt.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
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


    public void deleteNode(String path) {
        try {
            zooKeeper.delete(path, -1);
            System.out.println(LOG_PREFIX_OF_MAIN + "删除节点成功，path:" + path);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断指定节点是否存在
     * @param path 节点路径
     * @param needWatch
     * @return
     */
    public Stat exists(String path,boolean needWatch) {
        try {
            return zooKeeper.exists(path,needWatch);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取子节点
    public List<String> getChildren(String path, boolean needWatch) {
        try {
            return zooKeeper.getChildren(path, needWatch);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 删除所有节点
    public void deleteAllTestPath() {
        if(this.exists(CHILDREN_PATH,false) != null)
            this.deleteNode(CHILDREN_PATH);
        if(this.exists(PARENT_PATH,false) != null)
            this.deleteNode(PARENT_PATH);
    }



    // 收到来自Server的watcher通知后的处理
    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("进入 process 。。。。 event = " + watchedEvent);


        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(watchedEvent == null) return;

        // 连接状态
        Event.KeeperState keeperState = watchedEvent.getState();
        // 事件类型
        Event.EventType eventType = watchedEvent.getType();
        // 受影响的path
        String path = watchedEvent.getPath();

        String logPrefix = "【Watcher- " + this.seq.incrementAndGet() + "】";

        System.out.println(logPrefix + "收到 Watcher 通知");
        System.out.println(logPrefix + "连接状态：\t" + keeperState.toString());
        System.out.println(logPrefix + "事件类型：\t" + keeperState.toString());

        if(Event.KeeperState.SyncConnected == keeperState) {
            // 成功连接上 ZK服务器
            if(Event.EventType.None == eventType) {
                System.out.println(logPrefix + "成功连接上ZK服务器");
                countDownLatch.countDown();
            }
            // 创建节点
            else if(Event.EventType.NodeCreated == eventType) {
                System.out.println(logPrefix + "节点创建");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.exists(path,true);
            }
            // 更新节点
            else if(Event.EventType.NodeDataChanged == eventType) {
                System.out.println(logPrefix + "节点数据更新");
                System.out.println("我看看走不走这里");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(logPrefix + "数据内容：" + this.readData(PARENT_PATH,true));
            }
            // 更新子节点
            else if(Event.EventType.NodeChildrenChanged == eventType) {
                System.out.println(logPrefix + "子节点变更");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(logPrefix + "子节点列表：" + this.getChildren(PARENT_PATH,true));
            }
            // 删除节点
            else if(Event.EventType.NodeDeleted == eventType) {
                System.out.println(logPrefix + "节点" + path + "被删除");
            }
            else;
        } else if(Event.KeeperState.Disconnected == keeperState) {
            System.out.println(logPrefix + "与 ZK服务器断开连接");
        } else if(Event.KeeperState.AuthFailed == keeperState) {
            System.out.println(logPrefix + "权限检查失败");
        } else if(Event.KeeperState.Expired == keeperState) {
            System.out.println(logPrefix + "会话失效");
        } else ;
        System.out.println("===========================================================");
    }

    public static void main(String[] args) throws InterruptedException {
        // 建立 Watcher
        ZKWatcher zkWatcher = new ZKWatcher();
        // 创建连接
        zkWatcher.createConnection(CONNECTION_ADDR,SESSION_TIMEOUT);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 清理节点
        zkWatcher.deleteAllTestPath();

        if(zkWatcher.createPath(PARENT_PATH, System.currentTimeMillis() + "")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 读取数据
            System.out.println("--------------------------------read parent ----------------------------");

            // 读取子节点
            System.out.println("----------------------------------read children path-------------------------------");

            // 更新数据
            zkWatcher.upData(PARENT_PATH, System.currentTimeMillis() + "");

            Thread.sleep(1000);

            // 创建子节点
            zkWatcher.createPath(CHILDREN_PATH, System.currentTimeMillis() + "");

            Thread.sleep(1000);

            zkWatcher.upData(CHILDREN_PATH,System.currentTimeMillis() + "");
        }
        Thread.sleep(50000);
        // 清理节点
        zkWatcher.deleteAllTestPath();
        Thread.sleep(1000);
        zkWatcher.releaseConnection();
    }
}
