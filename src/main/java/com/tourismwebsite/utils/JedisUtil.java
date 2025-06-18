package com.tourismwebsite.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ResourceBundle;

/**
 * 包名:com.itheima.utils
 * 作者:Leevi
 * 日期2018-09-10  17:41
 */
public class JedisUtil {
    private static JedisPool jedisPool = null;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(50);
        config.setMaxIdle(10);
        config.setMinIdle(5);
        config.setMaxWaitMillis(5000);
        config.setTestOnBorrow(true);  // 获取连接时校验
        config.setTestOnReturn(true); // 归还时校验
        // 可以根据需要配置其它参数，比如testWhileIdle等

        // 如果有密码
        String host = "localhost";
        int port = 6379;
        int timeout = 2000;
        String password = null; // "yourpassword"; // 没密码可不写

        if (password != null && !"".equals(password)) {
            jedisPool = new JedisPool(config, host, port, timeout, password);
        } else {

        }

        jedisPool = new JedisPool(config, host, port, timeout);
       

    }

    public static Jedis getJedis() {
        
        return jedisPool.getResource();
    }

    public static void close(Jedis jedis) {
        if (jedis != null) {
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
