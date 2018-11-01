package com.roger.mapper;

import com.roger.SpringBaseTestSuit;
import com.roger.entity.DbDistriLock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDistributeLockMapper extends SpringBaseTestSuit {

    @Autowired(required = false)
    private DistributeLockMapper distributeLockMapper;

    @Test
    public void testInsertDistributeLock(){
        DbDistriLock distributeLock = new DbDistriLock();
        distributeLock.setId("1");
        distributeLock.setDistributeLockName("test");
        distributeLockMapper.delDistriLockByObject(distributeLock);
        long insertNum = distributeLockMapper.insertDistributeLock(distributeLock);
        Assert.assertTrue(insertNum == 1);

        long delNum = distributeLockMapper.delDistriLockByObject(distributeLock);
        Assert.assertTrue(delNum == 1);
    }
}
