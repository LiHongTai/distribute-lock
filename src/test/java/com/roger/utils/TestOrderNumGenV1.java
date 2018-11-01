package com.roger.utils;

import com.roger.SpringBaseTestSuit;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;

public class TestOrderNumGenV1 extends SpringBaseTestSuit {

    @Autowired(required = false)
    OrderNumGenV1 orderNumGen;

    private static int NUM = 50;

    private CountDownLatch latch = new CountDownLatch(NUM);

    @Test
    public void testGetOrderNumber() throws Exception{
        for(int i = 0 ; i < NUM; i ++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(orderNumGen.getOrderNumber());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },"Thread-" + i).start();

            latch.countDown();
        }
        latch.await();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
