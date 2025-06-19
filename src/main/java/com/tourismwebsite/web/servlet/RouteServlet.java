package com.tourismwebsite.web.servlet;

import com.tourismwebsite.domain.PageBean;
import com.tourismwebsite.domain.ResultInfo;
import com.tourismwebsite.domain.Route;
import com.tourismwebsite.domain.User;
import com.tourismwebsite.factory.BaseFactory;
import com.tourismwebsite.service.FavoriteService;
import com.tourismwebsite.service.RouteService;
import com.tourismwebsite.utils.StringUtil;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author j
 * @Date 2023/8/26 10:18
 * @Version 1.0
 */
@WebServlet(name = "RouteServlet", urlPatterns = "/route")
public class RouteServlet extends BaseServlet {
    private RouteService routeService = (RouteService) BaseFactory.getInterface("routeService");
    public ResultInfo hmCareChoose(HttpServletRequest request, HttpServletResponse response){
        ResultInfo resultInfo = new ResultInfo(true);
        Map<String,List<Route>> routeMapList = null;
        try {
            routeMapList = routeService.hmCareChoose();
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setFlag(false);
        }
        resultInfo.setData(routeMapList);
        return resultInfo;
    }

    public ResultInfo getRoutesByCid(HttpServletRequest request, HttpServletResponse response){
        ResultInfo resultInfo = new ResultInfo(true);
        
        Long cid = null;
        if (!StringUtil.isEmpty(request.getParameter("cid"))) {
            cid = Long.valueOf(request.getParameter("cid"));
        }

        Long currentPage = null;
        if (!StringUtils.isEmpty(request.getParameter("currentPage"))) {
            currentPage = Long.valueOf(request.getParameter("currentPage"));
        }

        String keyword = request.getParameter("keyword");
        String rname = request.getParameter("rname");
        String minPrice = request.getParameter("minPrice").replace(",","");
        String maxPrice = request.getParameter("maxPrice").replace(",","");

        try {
            PageBean<Route> routePageListBean = routeService.findByCidPageBean(cid,currentPage,keyword,rname,minPrice,maxPrice);
            resultInfo.setData(routePageListBean);
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setFlag(false);
        }

        return resultInfo;
    }
    public ResultInfo findRouteByRid(HttpServletRequest request, HttpServletResponse response){
        ResultInfo resultInfo = new ResultInfo(true);
        Long rid = null;
        if (!StringUtil.isEmpty(request.getParameter("rid"))) {
            rid = Long.valueOf(request.getParameter("rid"));
        }
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Route route = null;

        if (user != null){
            
            route = routeService.findRouteByRid(rid,user);
        }else {

            try {
                route = routeService.findRouteByRid(rid);
            } catch (Exception e) {
                e.printStackTrace();
                resultInfo.setData(-1);
                resultInfo.setFlag(false);
            }
        }

        resultInfo.setData(route);

        return resultInfo;
    }
    public ResultInfo favoriteRank(HttpServletRequest request, HttpServletResponse response){

        ResultInfo resultInfo = new ResultInfo(true);
//
        Long currentPage = Long.valueOf(request.getParameter("currentPage"));
//        String rname = request.getParameter("rname");
//        String minPrice = request.getParameter("minPrice").replace(",","");
//        String maxPrice = request.getParameter("maxPrice").replace(",","");
        try {
//            PageBean<Route> pageBean = routeService.findFavoriteRank2(currentPage,rname,minPrice,maxPrice);
            PageBean<Route> pageBean = routeService.findFavoriteRank(currentPage);
            resultInfo.setData(pageBean);
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setFlag(false);
        }

        return resultInfo;
    }

}
