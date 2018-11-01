package com.roger.lock.impl;

import com.roger.entity.DbDistriLock;
import com.roger.lock.DistributeLock;
import com.roger.mapper.DistributeLockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service("dbLock")
public class DbLock implements DistributeLock {

    @Autowired
    private DistributeLockMapper distributeLockMapper;

    @Override
    public void lock(String lockKey, String lockValue) {
       if(!tryLock(lockKey,lockValue)){
           //无法优雅的阻塞自己 -- 线程沉睡500ms
           waitForLock();
           //再次去尝试获取锁，直到获取成功
           lock(lockKey,lockValue);
       }
    }

    private void waitForLock() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {

        }
    }

    @Override
    public boolean tryLock(String lockKey, String lockValue) {
        DbDistriLock dbDistriLock = new DbDistriLock();
        dbDistriLock.setId(lockKey);
        dbDistriLock.setDistributeLockName(lockValue);
        try {
            distributeLockMapper.insertDistributeLock(dbDistriLock);
        }catch (Exception e){
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

    /**
     * 解铃还须系铃人，避免高并发情况下，误解锁的情况
     * @param lockKey
     * @param lockValue
     * @return
     */
    @Override
    public boolean unLock(String lockKey, String lockValue) {
        DbDistriLock dbDistriLock = new DbDistriLock();
        dbDistriLock.setId(lockKey);
        dbDistriLock.setDistributeLockName(lockValue);
        int delNum = distributeLockMapper.delDistriLockByObject(dbDistriLock);
        if(delNum == 1){
            //解锁成功
            return true;
        }
        return false;
    }
}
