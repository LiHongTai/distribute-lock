package com.roger.Lock;

import com.roger.SpringBaseTestSuit;
import com.roger.lock.DistributeLock;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

public class TestZKLockV1 extends SpringBaseTestSuit {

    @Resource(name = "zkLockV1")
    DistributeLock zkLockV1;

    private static int NUM = 2;
    private CountDownLatch latch = new CountDownLatch(NUM);

    @Test
    public void testLock() throws Exception{
        String lockPath = "/zk-book";
        for(int i = 0 ; i < NUM; i ++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        String threadName = Thread.currentThread().getName();
                        System.out.println(threadName + ":开始加锁");
                        String version = threadName.substring(threadName.lastIndexOf("-")+1);
                        zkLockV1.lock(lockPath,version);
                        System.out.println( Thread.currentThread().getName()+ ":加锁成功");
                        Thread.sleep(1000);
                        zkLockV1.unLock(lockPath,version);
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
