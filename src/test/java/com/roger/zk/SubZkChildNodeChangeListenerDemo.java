package com.roger.zk;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

public class SubZkChildNodeChangeListenerDemo {

    public static void main(String[] args) throws Exception{
        ZkClient zkClient = new ZkClient("127.0.0.1:2181",5000);
        String zkPath = "/zk-book";

        zkClient.subscribeChildChanges(zkPath, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> childNodePath) throws Exception {
                System.out.println(parentPath + "'s child node change; currentChildNodes:" + childNodePath);
            }
        });

        zkClient.createPersistent(zkPath);
        Thread.sleep(1000);

        zkClient.createEphemeral(zkPath + "/c1");

        Thread.sleep(5000);
        zkClient.delete(zkPath);

    }
}
