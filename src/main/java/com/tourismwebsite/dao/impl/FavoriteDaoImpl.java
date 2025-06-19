package com.tourismwebsite.dao.impl;

import com.tourismwebsite.dao.FavoriteDao;
import com.tourismwebsite.domain.Favorite;
import com.tourismwebsite.domain.Param;
import com.tourismwebsite.domain.Route;
import com.tourismwebsite.domain.User;
import com.tourismwebsite.utils.JDBCUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author j
 * @Date 2023/8/26 16:16
 * @Version 1.0
 */
public class FavoriteDaoImpl implements FavoriteDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteDao.class);

    private JdbcTemplate jdbcTemplate = new JdbcTemplate(JDBCUtil.getDataSource());
    @Override
    public int addFavorite(Long rid, String currentDate, int uid, JdbcTemplate jdbcTemplate) {
        String sql = "insert into tab_favorite (rid,date,uid) values(?,?,?)";
        int update = 0;
        try {
             update = jdbcTemplate.update(sql, rid, currentDate, uid);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        
        return update;
    }

    @Override
    public int findIsFavorite(int uid, Long rid) {
        String sql = "select count(1) from tab_favorite where rid = ? and uid = ?";

        int count = 0;
        try {
            count = jdbcTemplate.queryForObject(sql, Integer.class, rid, uid);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return count;
    }

    @Override
    public int delFavorite(Long rid, int uid, JdbcTemplate jdbcTemplate) {
        String sql = "delete from tab_favorite where rid = ? and uid = ?";
        int i = 0;
        try {
            i = jdbcTemplate.update(sql, rid, uid);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return i;
    }

    @Override
    public Long findMyFavoriteCount(User user) {
        String sql = "select count(1) from tab_favorite where uid = ?";
        Long count = null;
        try {
            count = jdbcTemplate.queryForObject(sql, Long.class, user.getUid());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public List<Favorite> findMyFavoritePageList(User user, Long currentPage, int pageSize) throws InvocationTargetException, IllegalAccessException {
        String sql = "select * from tab_favorite,tab_route where tab_favorite.rid = tab_route.rid and uid = ? limit ?,?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, user.getUid(), (currentPage - 1) * pageSize, pageSize);
        List<Favorite> favoriteList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            Favorite favorite = new Favorite();
            BeanUtils.populate(favorite,map);

            Route route = new Route();
            BeanUtils.populate(route,map);

            favorite.setRoute(route);

            favorite.setUser(user);
            favoriteList.add(favorite);

        }
        return favoriteList;
    }

    
    @Override
    public int batchAddFavorite(String currentDate, JdbcTemplate jdbcTemplate, List<Param> paramList) {

        int[] ints = new int[0];
        try {
            String sql1 = "truncate table tab_favorite";
            jdbcTemplate.execute(sql1);
            String sql2 = "insert into tab_favorite (rid,date,uid) values(?,?,?)";
            ints = jdbcTemplate.batchUpdate(sql2, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Param param = paramList.get(i);
                    ps.setInt(1, param.getRouteId());
                    ps.setString(2, currentDate);
                    ps.setInt(3, param.getUserId());
                }
    
                @Override
                public int getBatchSize() {
                    return paramList.size();
                }
            });
        } catch (DataAccessException e) {
            LOGGER.warn("插入用户时异常: {}",e.getMessage());
            throw new RuntimeException(e);
        }

        return ints.length;
    }
}
