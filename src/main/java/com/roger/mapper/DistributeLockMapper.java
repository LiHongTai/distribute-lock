package com.roger.mapper;

import com.roger.entity.DbDistriLock;

public interface DistributeLockMapper {

    int insertDistributeLock(DbDistriLock distributeLock);

    int delDistributeLock(String id);

    int delDistriLockByObject(DbDistriLock dbDistriLock);

}
