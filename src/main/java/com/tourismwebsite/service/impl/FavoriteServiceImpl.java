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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import redis.clients.jedis.Jedis;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @Author j
 * @Date 2023/8/26 16:15
 * @Version 1.0
 */
public class FavoriteServiceImpl implements FavoriteService {
    private FavoriteDao favoriteDao = (FavoriteDao) BaseFactory.getInterface("favoriteDao");
    private RouteDao routeDao = (RouteDao) BaseFactory.getInterface("routeDao");

    @Override
    public int addFavorite(Long rid, String testToken, int uid) throws SQLException {
        int count = 0;
        Connection connection = null;
        Jedis jedis = null;
        try {
            jedis = JedisUtil.getJedis();
            String lua_Script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long result = (Long) jedis.evalsha(jedis.scriptLoad(lua_Script), Arrays.asList(Constant.USER_FAVORITE_TOKEY_TEST + uid), Arrays.asList(testToken));

        if (result == 0L){
          count = -2;
        }else {
            DataSource dataSource = JDBCUtil.getDataSource();
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            TransactionSynchronizationManager.initSynchronization();//开启事务管理器
            connection = DataSourceUtils.getConnection(dataSource);//ThreadLocal 保证jdbcTemplate 和 connection 使用同一个连接,线程共享对象
            connection.setAutoCommit(false);

            favoriteDao.addFavorite(rid, DateUtil.getCurrentDate(), uid, jdbcTemplate);
            routeDao.updateRouteAddCount(rid, jdbcTemplate);
            connection.commit();
        }
        } catch (Exception e) {
                e.printStackTrace();
                if (connection != null){
                    connection.rollback();
                }
            }finally {
                //恢复自动提交事务,释放当前线程与连接对象的绑定
                TransactionSynchronizationManager.clearSynchronization();
                if (connection != null){
                    connection.setAutoCommit(true);
                }
                if (jedis != null){
                    jedis.close();
                }


            }

//            count = routeDao.findRouteCountByRid(rid);

        return  count;
    }

    @Override
    public Map<String,Object> findIsFavorite(int uid, Long rid) {

        int userIsFavorite = favoriteDao.findIsFavorite(uid,rid);

        Map<String, Object> resultMap = new HashMap<>();

        String token = UUID.randomUUID().toString().replace("-", "");
        Jedis jedis = JedisUtil.getJedis();


        jedis.set(Constant.USER_FAVORITE_TOKEY_TEST + uid,token,"NX","EX",60 * 30);
        String reidToken = jedis.get(Constant.USER_FAVORITE_TOKEY_TEST + uid);

        int count = routeDao.findRouteCountByRid(rid);

        resultMap.put("token",reidToken);
        resultMap.put("userIsFavorite",userIsFavorite > 0);
        resultMap.put("favoriteCount",count);

        return resultMap;
    }

    @Override
    public int cancelFavorite(int uid, Long rid, String testToken) throws SQLException {

        Connection connection = null;
        Jedis jedis = null;

        int count = 0;
        try {
            jedis = JedisUtil.getJedis();
            String lua_Script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long result = (Long) jedis.evalsha(jedis.scriptLoad(lua_Script), Arrays.asList(Constant.USER_FAVORITE_TOKEY_TEST + uid), Arrays.asList(testToken));

            if (result == 0L){
                count = -1;
            }else {

                DataSource dataSource = JDBCUtil.getDataSource();
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                TransactionSynchronizationManager.initSynchronization();
                connection = DataSourceUtils.getConnection(dataSource);
                connection.setAutoCommit(false);
                favoriteDao.delFavorite(rid,uid,jdbcTemplate);
                routeDao.updateRouteMinusCount(rid,jdbcTemplate);
                connection.commit();
            }

        } catch (Exception e) {
           if ( connection != null){
               connection.rollback();
           }
            e.printStackTrace();
        }finally {
            TransactionSynchronizationManager.clearSynchronization(); /*恢复自动提交事务,释放当前线程和对象的绑定*/
           if ( connection != null){
               connection.setAutoCommit(true);
           }
           if (jedis != null){
               JedisUtil.close(jedis);
           }

        }



        return count;
    }

    @Override
    public PageBean<Favorite> findMyFavorite(Long currentPage, User user) throws InvocationTargetException, IllegalAccessException {
        PageBean<Favorite> favoritePageBean = new PageBean<>();
        int pageSize = Constant.ROUTE_PAGESIZE;
        favoritePageBean.setPageSize(pageSize);
        Long totalSize = favoriteDao.findMyFavoriteCount(user);
        favoritePageBean.setTotalSize(totalSize);

        List<Favorite> favoriteList = favoriteDao.findMyFavoritePageList(user,currentPage,pageSize);
        favoritePageBean.setList(favoriteList);
        return favoritePageBean;
    }
}
