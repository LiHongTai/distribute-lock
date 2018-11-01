package com.roger.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.roger.SpringBaseTestSuit;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDruidDataSource extends SpringBaseTestSuit {

    @Autowired
    private DruidDataSource dataSource;

    @Test
    public void testInjectDruidDataSource() throws Exception{
        dataSource.getConnection();
        Assert.assertEquals(dataSource.getInitialSize(),dataSource.getCreateCount());
    }

}
