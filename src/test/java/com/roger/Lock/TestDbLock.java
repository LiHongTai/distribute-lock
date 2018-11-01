package com.roger.Lock;

import com.roger.SpringBaseTestSuit;
import com.roger.lock.DistributeLock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestDbLock extends SpringBaseTestSuit {

    @Resource(name = "dbLock")
    DistributeLock dbLock;

    private CountDownLatch latch = new CountDownLatch(5);

    @Test
    public void testTryLockWithTime(){
        dbLock.unLock("1","tryLock");
        boolean result = dbLock.tryLock("1","tryLock",3,TimeUnit.SECONDS);
        Assert.assertTrue(result);
        dbLock.unLock("1","tryLock");
    }

    @Test
    public void testConcurrentReqLock() throws Exception {
        dbLock.unLock("1","tryLock");
        for (int i = 1; i <= 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.countDown();
                        System.out.println(Thread.currentThread().getName() + ":开始加锁");
                        dbLock.lock("1","tryLock");
                        System.out.println(Thread.currentThread().getName() + ":加锁成功");
                        Thread.sleep(1000);
                        dbLock.unLock("1","tryLock");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, "thread-0" + i).start();

        }
        latch.await();
        Thread.sleep(Integer.MAX_VALUE);

    }

}
