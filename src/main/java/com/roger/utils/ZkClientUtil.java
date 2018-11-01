package com.roger.utils;

import org.I0Itec.zkclient.ZkClient;

public class ZkClientUtil {

    private static class SingletonZkClient{
        private static ZkClient INSTANCE = new ZkClient("127.0.0.1:2181",5000);
    }

    public static ZkClient getInstance(){
        return SingletonZkClient.INSTANCE;
    }
}
