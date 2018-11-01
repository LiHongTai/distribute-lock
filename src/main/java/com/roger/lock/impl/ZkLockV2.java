package com.roger.lock.impl;

import com.roger.lock.DistributeLock;
import com.roger.utils.ZkClientUtil;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 这种锁的使用方法不能是单例的
 *      每个线程必须有自己的锁对象，即线程独享对象
 */
public class ZkLockV2 implements DistributeLock {

    //当前节点
    private String currentPath;
    //上一个节点
    private String previousPath;

    @Override
    public void lock(String lockKey, String lockValue) {
        if(!tryLock(lockKey,lockValue)){
            //优雅的阻塞自己
            waitForLock(lockKey);
            //再次尝试获取锁
            lock(lockKey,lockValue);
        }
    }

    @Override
    public boolean tryLock(String lockKey, String lockValue) {
        //如果lockKey节点不存在，则创建要给持久节点
        //用来存数临时顺序节点
        if(!ZkClientUtil.getInstance().exists(lockKey)){
            try {
                ZkClientUtil.getInstance().createPersistent(lockKey);
            }catch (ZkNodeExistsException e){
                //高并发情况下，可能有多个线程同时进入创建持久节点
                //因此这里需要捕获异常，不需做任何处理
            }
        }

        //如果currentPath为null，则该线程还没有进入阻塞状态
        //即是第一次尝试获取锁
        if(currentPath == null) {
            //创建当前临时顺序节点
            currentPath = ZkClientUtil.getInstance().createEphemeralSequential(lockKey + "/", lockValue);
            currentPath = currentPath.substring(currentPath.lastIndexOf("/")+1);
        }

        List<String> childNodeList = ZkClientUtil.getInstance().getChildren(lockKey);
        Collections.sort(childNodeList);
        String minNodePath = childNodeList.get(0);
        if(minNodePath.equals(currentPath)){
            return true;
        }
        previousPath = childNodeList.get(childNodeList.indexOf(currentPath) - 1);
        return false;
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
        String oldValue = ZkClientUtil.getInstance().readData(lockKey + "/" + currentPath);
        if(lockValue.equals(oldValue)){
            return ZkClientUtil.getInstance().delete(lockKey + "/" + currentPath);
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
        ZkClientUtil.getInstance().subscribeDataChanges(lockKey + "/" + previousPath,iZkDataListener);
        //注册完成后，判断节点是否依然存在
        // 如果依然存在，则阻塞线程
        if(ZkClientUtil.getInstance().exists(lockKey + "/" + previousPath)){
            try {
                countDownLatch.await();
            }catch (InterruptedException e){

            }
        }
        //取消监听事件
        ZkClientUtil.getInstance().unsubscribeDataChanges(lockKey + "/" + previousPath,iZkDataListener);

    }
}
