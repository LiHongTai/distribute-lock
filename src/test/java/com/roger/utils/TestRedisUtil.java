package com.roger.utils;

import com.roger.SpringBaseTestSuit;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class TestRedisUtil extends SpringBaseTestSuit {

    @Test
    public void testJedisSinglon() {
        Jedis jedis1 = RedisUtil.getInstance();
        Jedis jedis2 = RedisUtil.getInstance();
        Assert.assertTrue(jedis1 == jedis2);
    }
}
