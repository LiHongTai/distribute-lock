package com.roger.lock.impl;

import com.roger.lock.DistributeLock;
import com.roger.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service("redisLock")
public class RedisLock implements DistributeLock{

    //key是唯一的
    private static final String LOCK_KEY = "lockKey";
    //有key是唯一的前提，配上这个参数，保证在同一时间，只能有一个客户端持有锁
    private static final String SET_IF_NOT_EXIST = "NX";
    //加锁的值不能是固定值，因为要保证可靠性
    //哪个客户端加的锁，需要哪个客户端解锁
    //自己不能把别人的锁给解了
    private static final String LOCK_VALUE = "lockValue";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;
    //保证不会发生死锁的情况，即使有一个客户端在持有锁期间崩溃，
    //但是没有主动解锁，到了过期时间，锁会自动解开
    private static final long EXPIRE_TIME = 180 * 1000;

    @Override
    public void lock(String lockKey,String reqestId) {
        if(!tryLock(lockKey,reqestId)) {
            //无法优雅的阻塞自己 -- 线程沉睡500ms
            waitForLock();
            //再次尝试获取锁
            lock(lockKey, reqestId);
        }
    }

    private void waitForLock() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {

        }
    }

    @Override
    public boolean tryLock(String lockKey,String reqestId) {
        //多线程环境，不能使用单例模式的jedis实例
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String retResult = jedis.set(lockKey, reqestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, EXPIRE_TIME);
        if (LOCK_SUCCESS.equals(retResult)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock(String lockKey,String requestId, long time, TimeUnit timeUnit) {
        if (tryLock(lockKey,requestId)) {
            return true;
        }
        if (time < 0) {
            return false;
        }
        long nanoTimeout = timeUnit.toNanos(time);
        long deadLine = System.nanoTime() + nanoTimeout;
        for (; ; ) {
            if (tryLock(lockKey,requestId)) {
                return true;
            }
            nanoTimeout = deadLine - System.nanoTime();
            if (nanoTimeout <= 0) {
                return false;
            }
        }
    }

    @Override
    public boolean unLock(String lockKey,String requestId) {
        StringBuffer scriptBuffer = new StringBuffer();
        scriptBuffer.append(" if ");
        scriptBuffer.append(" redis.call('get',KEYS[1]) == ARGV[1] ");
        scriptBuffer.append(" then ");
        scriptBuffer.append(" return redis.call('del',KEYS[1]) ");
        scriptBuffer.append(" else ");
        scriptBuffer.append(" return 0 ");
        scriptBuffer.append(" end");
        //多线程环境，不能使用单例模式的jedis实例
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        Object result = jedis.eval(scriptBuffer.toString(), Collections.singletonList(lockKey), Collections.singletonList(requestId));
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

}
