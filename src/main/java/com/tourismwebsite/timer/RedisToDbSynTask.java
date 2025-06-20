package com.tourismwebsite.timer;

import com.tourismwebsite.constant.Constant;
import com.tourismwebsite.dao.FavoriteDao;
import com.tourismwebsite.dao.RouteDao;
import com.tourismwebsite.domain.Param;
import com.tourismwebsite.factory.BaseFactory;
import com.tourismwebsite.utils.DateUtil;
import com.tourismwebsite.utils.JDBCUtil;
import com.tourismwebsite.utils.JedisUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import redis.clients.jedis.Jedis;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author J
 * @Date 2025/6/13  9:29
 * @Version 1.0
 */
@Component
public class RedisToDbSynTask implements Runnable {
    private Jedis jedis = JedisUtil.getJedis();
    private FavoriteDao favoriteDao;
    private RouteDao routeDao;
    
    
    public RedisToDbSynTask() {
    }

    public RedisToDbSynTask(Jedis jedis, FavoriteDao favoriteDao, RouteDao routeDao) {
        this.jedis = jedis;
        this.favoriteDao = favoriteDao;
        this.routeDao = routeDao;
    }
    
    
    @Override
    @Transactional
    public void run() {
        try {
            Set<String> userKeys = jedis.keys(Constant.USER_FAVORITE + "*");
            Map<Integer, Set<Integer>> parameters1 = new HashMap<>();
            for (String userKey : userKeys) {
                Set<String> rids = jedis.smembers(userKey);
                String[] split = userKey.split(":");
                parameters1.put(Integer.valueOf(split[2]),rids.stream().map(Integer::valueOf).collect(Collectors.toSet()));
            }

            List<Param> paramList = parameters1.entrySet().stream().flatMap(e -> e.getValue().stream().map(routeId -> {
                Param param = new Param();
                param.setUserId(e.getKey());
                param.setRouteId(routeId);
                return param;
            })).collect(Collectors.toList());
            
            Set<String> routeKeys = jedis.keys(Constant.ROUT_FAVORITE_RANK_DETAIL+"*:count");
            Map<Integer, Integer> parameters2 = new HashMap<>();
            for (String routeKey : routeKeys) {
                    String count = jedis.get(routeKey);
                    parameters2.put(Integer.valueOf(routeKey.split(":")[2]),Integer.valueOf(count));
           
                }
                    
            DataSource dataSource = JDBCUtil.getDataSource();
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            TransactionTemplate transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
            transactionTemplate.execute(start -> {
                routeDao.batchUpdateRouteCountNumber(parameters2, jdbcTemplate);
                favoriteDao.batchAddFavorite(DateUtil.getCurrentDate(), jdbcTemplate,paramList);
                return null;
            });
          
        } catch (NumberFormatException | TransactionException e) {
            throw new RuntimeException(e);
        }


    }
}
