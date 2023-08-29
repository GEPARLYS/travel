package com.tourismwebsite.dao.impl;

import com.tourismwebsite.dao.RouteDao;
import com.tourismwebsite.domain.Category;
import com.tourismwebsite.domain.Route;
import com.tourismwebsite.domain.RouteImg;
import com.tourismwebsite.domain.Seller;
import com.tourismwebsite.utils.JDBCUtil;
import com.tourismwebsite.utils.StringUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author j
 * @Date 2023/8/26 10:25
 * @Version 1.0
 */
public class RouteDaoImpl implements RouteDao {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(JDBCUtil.getDataSource());
    @Override
    public List<Route> findPopList() {
        String sql = "select * from tab_route where rflag = 1 order by count desc limit 0,4";
        List<Route> popList = null;
        try {
            popList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Route.class));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return popList;
    }

    @Override
    public List<Route> findNewList() {
        String sql = "select * from tab_route where rflag = 1 order by rdate desc limit 0,4";
        List<Route> newList = null;
        try {
            newList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Route.class));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return newList;
    }

    @Override
    public List<Route> findThemeList() {
        String sql = "select * from tab_route where rflag = 1 and isThemeTour = 1 limit 0,4";
        List<Route> themeList = null;
        try {
            themeList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Route.class));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return themeList;
    }

    @Override
    public Long findCountPage(Long cid, String keyword) {
        String sql = "select count(1) from tab_route where rflag = 1";
        ArrayList<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(cid)){
            sql += " and cid = ?";
            params.add(cid);
        }

        if (!StringUtil.isEmpty(keyword)){
            sql += " and rname = ?";
            params.add("%" + keyword + "%");
        }
        Long totalSize = null;
        try {
            totalSize = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return totalSize;
    }

    @Override
    public Route findRouteByRid(Long rid) {
        String sql = "select * from tab_route,tab_category,tab_seller where tab_route.cid = tab_category.cid and tab_route.sid = tab_seller.sid and rid = ?";
        Route route = null;
        try {
            Map<String, Object> routeMap = jdbcTemplate.queryForMap(sql, rid);
            route = new Route();
            BeanUtils.populate(route,routeMap);
            Seller seller = new Seller();
            BeanUtils.populate(seller,routeMap);
            route.setSeller(seller);
            Category category = new Category();
            BeanUtils.populate(category,routeMap);
            route.setCategory(category);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return route;
    }

    @Override
    public List<RouteImg> findRouteByRidImg(Long rid) {
        String sql = "select * from tab_route_img where rid = ?";
        List<RouteImg> routeImgList = null;
        try {
            routeImgList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RouteImg.class), rid);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return routeImgList;
    }

    @Override
    public int findRouteCountByRid(Long rid) {
        String sql = "select count from tab_route where rid = ?";
        Integer i = null;
        try {
            i = jdbcTemplate.queryForObject(sql, Integer.class, rid);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public void updateRouteAddCount(Long rid, JdbcTemplate jdbcTemplate) {
        String sql = "update tab_route set count = count + 1 where rid = ?";
        jdbcTemplate.update(sql, rid);
    }

    @Override
    public void updateRouteMinusCount(Long rid, JdbcTemplate jdbcTemplate) {
        String sql = "update tab_route set count = count - 1 where rid = ?";
        jdbcTemplate.update(sql, rid);
    }

    @Override
    public Long findFavoriteCount(String rname, String maxPrice, String minPrice) {

        String sql = "select count(*) from tab_route where rflag = 1";
        List<Object> params = new ArrayList<>();
        if (!StringUtil.isEmpty(rname)){
            sql += " and rname like ?";
            params.add("%"+rname+"%");
        }
        if (!StringUtil.isEmpty(minPrice)){
            sql += " and  price >= ?";
            params.add(minPrice);
        }

        if (!StringUtil.isEmpty(maxPrice)){
            sql += " and  price <= ?";
            params.add(maxPrice);
        }

        Long totalSize = null;
        try {
            totalSize = jdbcTemplate.queryForObject(sql, Long.class,params.toArray());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return totalSize;
    }

    @Override
    public List<Route> findFavoriteRankPageList(int pageSize, Long currentPage, String rname, String minPrice, String maxPrice) {
        String sql = "select * from tab_route where rflag = 1";
        List<Object> params = new ArrayList<>();
        if (!StringUtil.isEmpty(rname)){
            sql += " and rname like ?";
            params.add("%"+rname+"%");
        }
        if (!StringUtil.isEmpty(minPrice)){
            sql += " and price >= ?";
            params.add(minPrice);
        }

        if (!StringUtil.isEmpty(maxPrice)){
            sql += " and price <= ?";
            params.add(maxPrice);
        }

        params.add((currentPage - 1) * pageSize);
        params.add(pageSize);

        sql += " order by count desc limit ?,?";
        List<Route> routeList = null;
        try {
            routeList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Route.class), params.toArray());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return routeList;
    }

    @Override
    public List<Route> findPageList(Long cid, Long currentPage, Integer pageSize, String keyword) {
        String sql = "select * from tab_route where rflag = 1";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(cid)){
            sql += " and cid = ?";
            params.add(cid);
        }

        if (!StringUtil.isEmpty(keyword)){
            sql += " and rname like ?";
            params.add("%" + keyword + "%");
        }


        sql += " limit ?,?";
        params.add((currentPage - 1) * pageSize);
        params.add(pageSize);
        List<Route> pageList = null;
        try {
            pageList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Route.class), params.toArray());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return pageList;
    }
}
