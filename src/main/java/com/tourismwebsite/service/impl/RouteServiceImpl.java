package com.tourismwebsite.service.impl;

import com.tourismwebsite.constant.Constant;
import com.tourismwebsite.dao.RouteDao;
import com.tourismwebsite.domain.PageBean;
import com.tourismwebsite.domain.Route;
import com.tourismwebsite.domain.RouteImg;
import com.tourismwebsite.domain.User;
import com.tourismwebsite.factory.BaseFactory;
import com.tourismwebsite.service.RouteService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author j
 * @Date 2023/8/26 10:23
 * @Version 1.0
 */
public class RouteServiceImpl implements RouteService {
    private RouteDao routeDao = (RouteDao) BaseFactory.getInterface("routeDao");

    @Override
    public Map<String, List<Route>> hmCareChoose() {
        HashMap<String, List<Route>> routeListMap = new HashMap<>();
        List<Route> popList = routeDao.findPopList();
        routeListMap.put("popList",popList);

        List<Route> newList = routeDao.findNewList();
        routeListMap.put("newList",newList);

        List<Route> themeList = routeDao.findThemeList();
        routeListMap.put("themeList",themeList);
        return routeListMap;
    }

    @Override
    public PageBean<Route> findByCidPageBean(Long cid, Long currentPage, String keyword) {
        PageBean<Route> pageBean = new PageBean<>();
        pageBean.setCurrentPage(currentPage);
        Integer pageSize = 10;
        List<Route> routeList = routeDao.findPageList(cid,currentPage,pageSize,keyword);
        pageBean.setList(routeList);
        pageBean.setPageSize(pageSize);
        Long totalSize = routeDao.findCountPage(cid,keyword);
        pageBean.setTotalSize(totalSize);

        pageBean.setTotalPage(totalSize / pageSize == 0 ? (totalSize / pageSize) : (totalSize / pageSize) + 1);

        return pageBean;
    }

    @Override
    public Route findRouteByRid(Long rid) {


        Route route = routeDao.findRouteByRid(rid);

       List<RouteImg> routeImgList = routeDao.findRouteByRidImg(rid);

       route.setRouteImgList(routeImgList);
        return route;
    }


    @Override
    public PageBean<Route> findFavoriteRank(Long currentPage, String rname, String minPrice, String maxPrice) {
        PageBean<Route> pageBean = new PageBean<>();
        int pageSize = Constant.ROUTE_PAGESIZE;
        pageBean.setPageSize(pageSize);
        Long totalSize = routeDao.findFavoriteCount(rname,maxPrice,minPrice);
        pageBean.setTotalSize(totalSize);
        pageBean.setCurrentPage(currentPage);
        List<Route> routeList = routeDao.findFavoriteRankPageList(pageSize,currentPage,rname,minPrice,maxPrice);
        pageBean.setList(routeList);
        pageBean.setTotalPage(totalSize / pageSize == 0 ? totalSize / pageSize : (totalSize / pageSize) + 1);

        return pageBean;
    }

    @Override
    public Route findRouteByRid(Long rid, User user) {

        Route route = routeDao.findRouteByRid(rid);

        List<RouteImg> routeImgList = routeDao.findRouteByRidImg(rid);

        route.setRouteImgList(routeImgList);
        return route;
    }

    @Override
    public int findRouteCountByRid(Long rid) {
        return routeDao.findRouteCountByRid(rid);
    }
}
