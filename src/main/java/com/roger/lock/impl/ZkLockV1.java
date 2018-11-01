package com.roger.lock.impl;

import com.roger.lock.DistributeLock;
import com.roger.utils.ZkClientUtil;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service("zkLockV1")
public class ZkLockV1 implements DistributeLock {

    @Override
    public void lock(String lockKey, String lockValue) {
        if(!tryLock(lockKey,lockValue)){
            //优雅的阻塞自己
            waitForLock(lockKey);
            //再次尝试获取锁，直到获取锁成功
            lock(lockKey,lockValue);
        }
    }


    @Override
    public boolean tryLock(String lockKey, String lockValue) {
        try {
            ZkClientUtil.getInstance().createEphemeral(lockKey, lockValue);
        }catch (ZkNodeExistsException e){
            return false;
        }
        return true;
    }

    @Override
    public boolean tryLock(String lockKey, String lockValue, long time, TimeUnit timeUnit) {
        if(tryLock(lockKey,lockValue)){
            return true;
        }

        if(time <= 0){
            return false;
        }

        long nanoTimes = timeUnit.toNanos(time);
        long deadlineTime = System.nanoTime() + nanoTimes;
        for (;;){
            if(tryLock(lockKey,lockValue)){
                return true;
            }

            nanoTimes = deadlineTime - System.nanoTime();
            if(nanoTimes <= 0){
                return false;
            }
        }
    }

    @Override
    public boolean unLock(String lockKey, String lockValue) {
        String oldValue = ZkClientUtil.getInstance().readData(lockKey);
        if(lockValue.equals(oldValue)){
            return ZkClientUtil.getInstance().delete(lockKey);
        }
        return false;
    }

    private void waitForLock(String lockKey) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //注册监听事件
        IZkDataListener iZkDataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                //当节点被删除后，唤醒阻塞中的线程，再次竞争共享资源
                countDownLatch.countDown();
            }
        };
        ZkClientUtil.getInstance().subscribeDataChanges(lockKey,iZkDataListener);
        //注册完成后，判断节点是否依然存在
        // 如果依然存在，则阻塞线程
        if(ZkClientUtil.getInstance().exists(lockKey)){
            try {
                countDownLatch.await();
            }catch (InterruptedException e){

            }
        }
        //取消监听事件
        ZkClientUtil.getInstance().unsubscribeDataChanges(lockKey,iZkDataListener);

    }
}
