package com.tourismwebsite.service.impl;

import com.tourismwebsite.constant.Constant;
import com.tourismwebsite.dao.FavoriteDao;
import com.tourismwebsite.dao.RouteDao;
import com.tourismwebsite.domain.Favorite;
import com.tourismwebsite.domain.PageBean;
import com.tourismwebsite.domain.User;
import com.tourismwebsite.factory.BaseFactory;
import com.tourismwebsite.service.FavoriteService;
import com.tourismwebsite.utils.DateUtil;
import com.tourismwebsite.utils.JDBCUtil;
import com.tourismwebsite.utils.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author j
 * @Date 2023/8/26 16:15
 * @Version 1.0
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {
    private FavoriteDao favoriteDao = (FavoriteDao) BaseFactory.getInterface("favoriteDao");
    private RouteDao routeDao = (RouteDao) BaseFactory.getInterface("routeDao");




    @Override
    public int addFavorite(Long rid, int uid) throws SQLException {
        int count = 0;
        try (Jedis jedis = JedisUtil.getJedis();){
            String luaScript = "if redis.call('SADD', KEYS[1], ARGV[1]) == 1 then " +
                    "redis.call('INCR', KEYS[2]); return 1; " +
                    "else return 0; end";
            
            List<String> keys = Arrays.asList("favorite:user:" + uid, "favorite:route:" + rid + ":count");
            List<String> argsList = Arrays.asList(String.valueOf(rid));

            Long result = (Long) jedis.eval(luaScript, keys, argsList);
            if (result == 1L) {

//                DataSource dataSource = JDBCUtil.getDataSource();
//                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//                TransactionTemplate transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
//                transactionTemplate.execute(start -> {
//                    favoriteDao.addFavorite(rid, DateUtil.getCurrentDate(), uid, jdbcTemplate);
//                    routeDao.updateRouteAddCount(rid, jdbcTemplate);
//                    return null;
//                });
                count = 1;
              
            } 
               
             /*   String token = UUID.randomUUID().toString().replace("-", "");
                jedis.set(Constant.USER_FAVORITE_TOKEY_TEST + uid, token, "NX", "EX", 60 * 30);*/
                
//            TransactionSynchronizationManager.initSynchronization();//开启事务管理器
//            connection = DataSourceUtils.getConnection(dataSource);//ThreadLocal 保证jdbcTemplate 和 connection 使用同一个连接,线程共享对象
//            connection.setAutoCommit(false);
//
//            favoriteDao.addFavorite(rid, DateUtil.getCurrentDate(), uid, jdbcTemplate);
//            routeDao.updateRouteAddCount(rid, jdbcTemplate);
//            connection.commit();

        } catch (Exception e) {
            e.printStackTrace();
//                if (connection != null){
//                    connection.rollback();
//                }
           
        }

        return count;
    }

    @Override
    public int cancelFavorite(int uid, Long rid) {
        int count = 0;
        try(Jedis jedis = JedisUtil.getJedis()) {
            //        Connection connection = null;
         
            String luaScript = "if redis.call('SREM', KEYS[1], ARGV[1]) == 1 then " +
                    "redis.call('DECR', KEYS[2]); return 1;" +
                    "else return 0; end";
            
            int expirationTimeInSeconds = 60 * 60 * 24;       
            List<String> keys = Arrays.asList(Constant.USER_FAVORITE + uid, Constant.USER_FAVORITE_COUNT + rid + ":count");
            List<String> argsList = Arrays.asList(String.valueOf(rid));
            Long result = (Long) jedis.eval(luaScript, keys, argsList);
            if (result == 1L) {
                
                
/*
                DataSource dataSource = JDBCUtil.getDataSource();
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                *//*Long del = jedis.del(Constant.USER_FAVORITE_TOKEY_TEST + uid);*//*

                TransactionTemplate transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
                transactionTemplate.execute(status -> {
                    favoriteDao.delFavorite(rid, uid, jdbcTemplate);
                    routeDao.updateRouteMinusCount(rid, jdbcTemplate);
                    return null;
                });*/
                
                count = 1;
            } 


             
//                TransactionSynchronizationManager.initSynchronization();
//                connection = DataSourceUtils.getConnection(dataSource);
//                connection.setAutoCommit(false);
//                favoriteDao.delFavorite(rid,uid,jdbcTemplate);
//                routeDao.updateRouteMinusCount(rid,jdbcTemplate);
//                connection.commit();
        } catch (Exception e) {
//            if ( connection != null){
//                connection.rollback();
//            }
            e.printStackTrace();
        } finally {
//            TransactionSynchronizationManager.clearSynchronization(); /*恢复自动提交事务,释放当前线程和对象的绑定*/
//            if ( connection != null){
//                connection.setAutoCommit(true);
//            }
        }

        return count;
    }


    @Override
    public Map<String, Object> findIsFavorite(int uid, Long rid) {
        
        Map<String, Object> resultMap = new HashMap<>();
        try(Jedis jedis = JedisUtil.getJedis()){
            Set<String> userKeys = jedis.keys(Constant.USER_FAVORITE+uid);

            if (userKeys.isEmpty()){
                resultMap.put("userIsFavorite", false);
               
            }else {
                for (String userKey : userKeys) {
                    Set<String> rids = jedis.smembers(userKey);
                    Set<Long> collect = rids.stream().map(Long::valueOf).collect(Collectors.toSet());
                    boolean contains = collect.contains(rid);
                    resultMap.put("userIsFavorite", contains);
                }
                
            }
            String count = jedis.get(Constant.USER_FAVORITE_COUNT+rid+":count");
            resultMap.put("favoriteCount",(count == null || count.isEmpty()) ? 0 : Integer.parseInt(count));
           
            
        }
        return resultMap;
    }
    
    private int findDbIsFavorite(int uid, Long rid){
        
        return favoriteDao.findIsFavorite(uid, rid);
    }
    
    private int findDaRouteCount(Long rid){
        return routeDao.findRouteCountByRid(rid);
    }


    @Override
    public PageBean<Favorite> findMyFavorite(Long currentPage, User user) throws InvocationTargetException, IllegalAccessException {
        PageBean<Favorite> favoritePageBean = new PageBean<>();
        int pageSize = Constant.ROUTE_PAGESIZE;
        favoritePageBean.setPageSize(pageSize);
        Long totalSize = favoriteDao.findMyFavoriteCount(user);
        favoritePageBean.setTotalSize(totalSize);
        favoritePageBean.setCurrentPage(currentPage);
        List<Favorite> favoriteList = favoriteDao.findMyFavoritePageList(user, currentPage, pageSize);
        favoritePageBean.setList(favoriteList);
        return favoritePageBean;
    }

}
