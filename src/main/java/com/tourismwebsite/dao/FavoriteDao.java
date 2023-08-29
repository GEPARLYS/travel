package com.tourismwebsite.dao;

import com.tourismwebsite.domain.Favorite;
import com.tourismwebsite.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @Author j
 * @Date 2023/8/26 16:16
 * @Version 1.0
 */
public interface FavoriteDao {
    int addFavorite(Long rid, String currentDate, int uid, JdbcTemplate jdbcTemplate);

    int findIsFavorite(int uid, Long rid);

    int delFavorite(Long rid, int uid, JdbcTemplate jdbcTemplate);

    Long findMyFavoriteCount(User user);

    List<Favorite> findMyFavoritePageList(User user, Long currentPage, int pageSize) throws InvocationTargetException, IllegalAccessException;

}
