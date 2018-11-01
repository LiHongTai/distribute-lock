package com.roger.zk;

import org.I0Itec.zkclient.ZkClient;

import java.util.Collections;
import java.util.List;

public class ZkEphemeralSequenNodeDemo {

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181",5000);
        String zkParentPath = "/zk-book";
        if(!zkClient.exists(zkParentPath)){
            zkClient.createPersistent(zkParentPath);
        }
        for(int i = 0; i < 10; i++){
            zkClient.createEphemeralSequential(zkParentPath + "/",i);
        }
        List<String> childNodeList = zkClient.getChildren(zkParentPath);
        Collections.sort(childNodeList);
        for(String childNode : childNodeList){
            System.out.println(childNode);
        }

        System.out.println("0000000023上一个节点为:" + childNodeList.get(childNodeList.indexOf("0000000023") - 1));
    }
}
