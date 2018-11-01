package com.roger.zk;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.TimeUnit;

public class SubZkNodeDataChangeListenerDemo {

    public static void main(String[] args) throws Exception{
        ZkClient zkClient = new ZkClient("127.0.0.1:2181",5000);

        String zkPath = "/zk-book";
        zkClient.createEphemeral(zkPath,"123");

        zkClient.subscribeDataChanges(zkPath, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                //节点数据发生改变
                System.out.println("Node " + dataPath + " changed. new data " + data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                //节点被删除
                System.out.println("Node " + dataPath + " deleted.");
            }
        });

        System.out.println((String) zkClient.readData(zkPath));
        zkClient.writeData(zkPath,"456");
        TimeUnit.SECONDS.sleep(1);
        zkClient.delete(zkPath);

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }
}
