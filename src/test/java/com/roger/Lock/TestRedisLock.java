package com.roger.Lock;

import com.roger.SpringBaseTestSuit;
import com.roger.lock.DistributeLock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

public class TestRedisLock extends SpringBaseTestSuit {

    @Resource(name = "redisLock")
    private DistributeLock redisLock;

    private CountDownLatch latch = new CountDownLatch(5);

    @Test
    public void testLock() {
        String lockKey = "lockKey";
        System.out.println("准备获取锁!");
        String requestId = String.valueOf(System.nanoTime());
        redisLock.lock(lockKey,requestId);
        System.out.println("获取锁成功!");

        boolean result = redisLock.unLock(lockKey,requestId);
        Assert.assertTrue(result);

    }

    @Test
    public void testConcurrentReqLock() throws Exception {
        String lockKey = "lockKey";
        for (int i = 1; i <= 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.countDown();
                        System.out.println(Thread.currentThread().getName() + ":开始加锁");
                        String requestId = String.valueOf(System.nanoTime());
                        redisLock.lock(lockKey,requestId);
                        System.out.println(Thread.currentThread().getName() + ":加锁成功");
                        Thread.sleep(1000);
                        redisLock.unLock(lockKey,requestId);
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
