package com.roger.Lock;

import com.roger.lock.DistributeLock;
import com.roger.lock.impl.ZkLockV1;
import com.roger.lock.impl.ZkLockV2;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

public class TestZkLockV2 {

    private static int NUM = 50;
    private CountDownLatch latch = new CountDownLatch(NUM);

    @Test
    public void testLock() throws Exception{
        String lockPath = "/zk-book";
        for(int i = 0 ; i < NUM; i ++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DistributeLock zkDistributeLockV1 = new ZkLockV1();
                        String threadName = Thread.currentThread().getName();
                        System.out.println(threadName + ":开始加锁");
                        String version = threadName.substring(threadName.lastIndexOf("-")+1);
                        zkDistributeLockV1.lock(lockPath,version);
                        System.out.println( Thread.currentThread().getName()+ ":加锁成功");
                        Thread.sleep(1000);
                        zkDistributeLockV1.unLock(lockPath,version);
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
