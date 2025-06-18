package com.tourismwebsite.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourismwebsite.constant.Constant;
import com.tourismwebsite.dao.RouteDao;
import com.tourismwebsite.domain.PageBean;
import com.tourismwebsite.domain.Route;
import com.tourismwebsite.domain.RouteImg;
import com.tourismwebsite.domain.User;
import com.tourismwebsite.factory.BaseFactory;
import com.tourismwebsite.service.RouteService;
import com.tourismwebsite.utils.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.*;
import java.util.stream.Collectors;

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


//    @Override
    public PageBean<Route> findFavoriteRank2(Long currentPage, String rname, String minPrice, String maxPrice) {
        
        //todo 之前DB查询排行榜
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
    public PageBean<Route> findFavoriteRank(Long currentPage, String rname, String minPrice, String maxPrice) {
        int page = currentPage == null ? 1 : currentPage.intValue();
        int pageSize = 8;
        int start = (page - 1) * pageSize;
        int stop = start + pageSize - 1;

        PageBean<Route> pageBean = new PageBean<>();
        pageBean.setPageSize(pageSize);
        pageBean.setCurrentPage(currentPage);
        
        try(Jedis jedis = JedisUtil.getJedis()) {
            Set<Tuple> top8 = jedis.zrevrangeWithScores("route:rank", start, stop);
            if (top8.isEmpty()){
                List<Route> routes = queryDbFavoriteRanK();
                saveRedisFavoriteRank(routes,jedis);
                List<Route> pageList = routes.stream().skip(start).limit(pageSize).collect(Collectors.toList());//只获取第一页
                pageBean.setList(pageList);
                pageBean.setTotalSize((long)routes.size());
                pageBean.setTotalPage((long) ((routes.size() + pageSize - 1) / pageSize));
            }else {
                ObjectMapper mapper = new ObjectMapper();
                List<String> ids = top8.stream().map(Tuple::getElement).collect(Collectors.toList());
//                List<String> fielids = jedis.hmget("route:rank:data", ids.toArray(new String[0]));
                List<Route> routes = new ArrayList<>();

//                Set<Tuple> tuples = jedis.zrevrangeWithScores("route:rank", start, stop);
                List<String> details = jedis.hmget("route:rank:data", top8.stream().map(Tuple::getElement).toArray(String[]::new));
                for (int i = 0; i < ids.size(); i++) {
                    String id = ids.get(i);
                    String detailJson = details.get(i);
                    Route route = mapper.readValue(detailJson, Route.class);
                    route.setRid(Integer.parseInt(id));
                    routes.add(route);
                    
                }
                long count = jedis.zcard("route:rank");

                pageBean.setList(routes);
                pageBean.setTotalSize(count);
                pageBean.setTotalPage((count + pageSize - 1) / pageSize);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return pageBean;
    }
    
    private List<Route>  queryDbFavoriteRanK(){
        return routeDao.findFavoriteRankCount();
    }
    
    private void saveRedisFavoriteRank(List<Route> routes,Jedis jedis){
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> hashMap = new HashMap<>();
        Map<String, Integer> zsetMap = new HashMap<>();
        
        routes.forEach(item -> {
            Map<String, Object> data = new HashMap<>();
            data.put("price",item.getPrice());
            data.put("count",item.getCount());
            data.put("rimage",item.getRimage());
            data.put("rname",item.getRname());
            try {
                String json = mapper.writeValueAsString(data);
                hashMap.put(String.valueOf(item.getRid()),json);
                zsetMap.put(String.valueOf(item.getRid()),item.getCount());
                
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            
        });
        //batch storage hash
        jedis.hmset("route:rank:data",hashMap);
        //batch storage zset
        zsetMap.forEach((key,value) -> {
            jedis.zadd("route:rank",value,key);
        });




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

        Set<String> routeKeys;
        try(Jedis jedis = JedisUtil.getJedis()){
            routeKeys = jedis.keys("favorite:route:"+rid+":count");
        }

        return routeKeys.size();  
    }
}
