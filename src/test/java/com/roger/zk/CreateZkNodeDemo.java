package com.roger.zk;

import org.I0Itec.zkclient.ZkClient;

public class CreateZkNodeDemo {


    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181",5000);

        String zkPath = "/zk-book";
        zkClient.createPersistent(zkPath,true);
        zkPath = "/zk-book/c1";
        zkClient.createEphemeral(zkPath,new Integer(1));
        zkClient.writeData(zkPath,new Integer(1));
    }
}
