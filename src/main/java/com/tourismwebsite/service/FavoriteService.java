package com.tourismwebsite.service;

import com.tourismwebsite.domain.Favorite;
import com.tourismwebsite.domain.PageBean;
import com.tourismwebsite.domain.Route;
import com.tourismwebsite.domain.User;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;

/**
 * @Author j
 * @Date 2023/8/26 16:15
 * @Version 1.0
 */
public interface FavoriteService {
    int addFavorite(Long rid, String testToken, int uid) throws SQLException;

    Map<String,Object> findIsFavorite(int uid, Long rid);

    int cancelFavorite(int uid, Long rid, String testToken) throws SQLException;

    PageBean<Favorite> findMyFavorite(Long currentPage, User user) throws InvocationTargetException, IllegalAccessException;
}
