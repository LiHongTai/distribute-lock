package com.roger.lock;

import java.util.concurrent.TimeUnit;

public interface DistributeLock {

    void lock(String lockKey,String lockValue);

    boolean tryLock(String lockKey,String lockValue);

    boolean tryLock(String lockKey,String lockValue,long time, TimeUnit timeUnit);

    boolean unLock(String lockKey,String lockValue);

}
