package com.roger.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

public class RedisUtil {

    private static class SingletonJedis {
        private static final Jedis INSTANCE = new Jedis("127.0.0.1", 6379);

    }

    public static Jedis getInstance() {
        return SingletonJedis.INSTANCE;
    }
}
