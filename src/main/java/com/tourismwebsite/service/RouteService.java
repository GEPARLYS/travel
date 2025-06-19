package com.tourismwebsite.service;

import com.tourismwebsite.domain.PageBean;
import com.tourismwebsite.domain.Route;
import com.tourismwebsite.domain.User;

import java.util.List;
import java.util.Map;

/**
 * @Author j
 * @Date 2023/8/26 10:23
 * @Version 1.0
 */
public interface RouteService {
    Map<String,List<Route>> hmCareChoose();

    PageBean<Route> findByCidPageBean(Long cid, Long currentPage, String keyword, String rname, String minPrice, String maxPrice);

    Route findRouteByRid(Long rid);

//    PageBean<Route> findFavoriteRank2(Long currentPage, String rname, String minPrice, String maxPrice);
    PageBean<Route> findFavoriteRank(Long currentPage);

    Route findRouteByRid(Long rid, User user);

    int findRouteCountByRid(Long rid);

}
