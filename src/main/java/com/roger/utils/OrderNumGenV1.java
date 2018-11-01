package com.roger.utils;

import com.roger.lock.DistributeLock;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class OrderNumGenV1 {

    private static int count = 0;

    private static final String DATA_FORMAT = "yyyy-MM-dd-HH-mm-ss";
    private static final String zkPath = "/zk-book";

    @Resource(name = "zkLockV1")
    private DistributeLock lock;

    public String getOrderNumber(){
        String nanoTimes = String.valueOf(System.nanoTime());
        lock.lock(zkPath,nanoTimes);
        try {
            String prefix = new SimpleDateFormat(DATA_FORMAT).format(new Date());
            return prefix + "-" + (++count);
        }finally {
            lock.unLock(zkPath,nanoTimes);
        }
    }
}
