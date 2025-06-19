package com.tourismwebsite.listener;

import com.tourismwebsite.dao.FavoriteDao;
import com.tourismwebsite.dao.RouteDao;
import com.tourismwebsite.factory.BaseFactory;
import com.tourismwebsite.timer.RedisToDbSynTask;
import com.tourismwebsite.utils.CalculateUtil;
import com.tourismwebsite.utils.JedisUtil;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author J
 * @Date 2025/6/13  11:16
 * @Version 1.0
 */
public class MyAppInitListener implements ServletContextListener {
    private Jedis jedis = JedisUtil.getJedis();
    private FavoriteDao favoriteDao = (FavoriteDao) BaseFactory.getInterface("favoriteDao");
    private RouteDao routeDao = (RouteDao) BaseFactory.getInterface("routeDao");
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler.scheduleAtFixedRate(
                new RedisToDbSynTask(jedis,favoriteDao,routeDao),
                0,
                CalculateUtil.getExpireSeconds(),
                TimeUnit.SECONDS
        );

       
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        jedis.close();
        scheduler.shutdown();
    }
}
