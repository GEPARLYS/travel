package com.tourismwebsite.dao;

import com.tourismwebsite.domain.Route;
import com.tourismwebsite.domain.RouteImg;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * @Author j
 * @Date 2023/8/26 10:24
 * @Version 1.0
 */
public interface RouteDao {
    List<Route> findPopList();

    List<Route> findNewList();

    List<Route> findThemeList();

    List<Route> findPageList(Long cid, Long currentPage, Integer pageSize, String keyword);

    Long findCountPage(Long cid, String keyword);

    Route findRouteByRid(Long rid);

    List<RouteImg> findRouteByRidImg(Long rid);

    int findRouteCountByRid(Long rid);

    void updateRouteAddCount(Long rid, JdbcTemplate jdbcTemplate);

    void updateRouteMinusCount(Long rid, JdbcTemplate jdbcTemplate);

    Long findFavoriteCount(String rname, String maxPrice, String minPrice);

    List<Route> findFavoriteRankPageList(int pageSize, Long currentPage, String rname, String minPrice, String maxPrice);

    void batchUpdateRouteCountNumber(Map<Integer, Integer> parameters2, JdbcTemplate jdbcTemplate);

    List<Route> findFavoriteRankCount();
    
}
