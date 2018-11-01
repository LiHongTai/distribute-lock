package com.roger.utils;

import com.roger.lock.DistributeLock;
import com.roger.lock.impl.ZkLockV2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class OrderNumGenV2 {
    private static int count = 0;

    private static final String DATA_FORMAT = "yyyy-MM-dd-HH-mm-ss";
    private static final String zkPath = "/zk-book";


    public String getOrderNumber() {
        DistributeLock lock = new ZkLockV2();
        String nanoTimes = String.valueOf(System.nanoTime());
        lock.lock(zkPath, nanoTimes);
        try {
            String prefix = new SimpleDateFormat(DATA_FORMAT).format(new Date());
            return prefix + "-" + (++count);
        } finally {
            lock.unLock(zkPath, nanoTimes);
        }
    }
}
