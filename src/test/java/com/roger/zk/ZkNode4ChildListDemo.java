package com.roger.zk;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

public class ZkNode4ChildListDemo {

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181",5000);
        String zkParentPath = "/zk-book";
        if(!zkClient.exists(zkParentPath)){
            zkClient.createPersistent(zkParentPath);
        }

        zkClient.createEphemeral(zkParentPath + "/c1");
        zkClient.createEphemeral(zkParentPath + "/c2");

        List<String> zkChildNodeList = zkClient.getChildren(zkParentPath);
        for (String childNode: zkChildNodeList) {
            System.out.println(childNode);
        }
        zkClient.deleteRecursive(zkParentPath);
    }
}
