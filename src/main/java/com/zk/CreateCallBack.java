package com.zk;

import org.apache.zookeeper.AsyncCallback;

/**
 * Created by lenovo on 2019/1/12.
 */
public class CreateCallBack implements AsyncCallback.StringCallback {
    @Override
    public void processResult(int rc, String path, Object o, String name) {
        System.out.println("创建节点：" + path);
        System.out.println((String) o);
    }
}
