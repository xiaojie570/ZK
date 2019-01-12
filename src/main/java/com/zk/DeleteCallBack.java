package com.zk;

import org.apache.zookeeper.AsyncCallback;

/**
 * Created by lenovo on 2019/1/12.
 */
public class DeleteCallBack implements AsyncCallback.VoidCallback{
    @Override
    public void processResult(int rc, String path, Object ctx) {
        System.out.println("删除节点" + path);
        System.out.println((String)ctx);
    }
}
