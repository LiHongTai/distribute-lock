package com.roger.utils;

import com.roger.SpringBaseTestSuit;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Assert;
import org.junit.Test;

public class TestZkClientUtil extends SpringBaseTestSuit {

    @Test
    public void testSingletonZkClient(){
        ZkClient zkClient = ZkClientUtil.getInstance();
        Assert.assertNotNull(zkClient);
    }
}
