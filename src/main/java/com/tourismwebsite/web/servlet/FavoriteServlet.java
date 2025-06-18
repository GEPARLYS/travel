package com.tourismwebsite.web.servlet;

import com.tourismwebsite.domain.*;
import com.tourismwebsite.factory.BaseFactory;
import com.tourismwebsite.service.FavoriteService;
import com.tourismwebsite.service.RouteService;
import com.tourismwebsite.utils.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author j
 * @Date 2023/8/26 16:09
 * @Version 1.0
 */
@WebServlet(name = "FavoriteServlet", urlPatterns = "/favorite")
public class FavoriteServlet extends BaseServlet {
    private FavoriteService favoriteService = (FavoriteService) BaseFactory.getInterface("favoriteService");
    private RouteService routeService = (RouteService) BaseFactory.getInterface("routeService");
    public ResultInfo addFavorite(HttpServletRequest request, HttpServletResponse response){
        
        ResultInfo resultInfo = new ResultInfo(true);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null){
            resultInfo.setData(-1);
        }else{
            Long rid = Long.valueOf(request.getParameter("rid"));
            try {
                int countFavorite = favoriteService.addFavorite(rid,user.getUid());
                resultInfo.setData(countFavorite);
            } catch (Exception e) {
                e.printStackTrace();
                resultInfo.setFlag(false);
                resultInfo.setData(null);
            }
        }
        return resultInfo;
    }

    public ResultInfo cancelFavorite(HttpServletRequest request, HttpServletResponse response){
        ResultInfo resultInfo = new ResultInfo(true);
        HttpSession session = request.getSession();
        Long rid = null;
        if (!StringUtil.isEmpty(request.getParameter("rid"))) {
             rid = Long.valueOf(request.getParameter("rid"));
        }
        User user = (User) session.getAttribute("user");
        if (user == null){
           resultInfo.setData(-1);
        }else {
            try {
                int count = favoriteService.cancelFavorite(user.getUid(),rid);
                resultInfo.setData(count);
            } catch (Exception e) {
                e.printStackTrace();
                resultInfo.setData(null);
                resultInfo.setFlag(false);
            }
        }
       
        return resultInfo;
    }

    public ResultInfo isFavorite(HttpServletRequest request, HttpServletResponse response){
        ResultInfo resultInfo = new ResultInfo(true);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Long rid = Long.valueOf(request.getParameter("rid"));
        if (user == null){
            resultInfo.setFlag(false);
            HashMap<String, Object> resultMap = new HashMap<>();
            int count = routeService.findRouteCountByRid(rid);
            resultMap.put("userIsFavorite",false);
            resultMap.put("favoriteCount",count);
            resultInfo.setData(resultMap);
        }else{
            Map<String,Object> map = favoriteService.findIsFavorite(user.getUid(),rid);
//            int count = (int) map.get("count");
//            if (map.get("token") != null){
//                String token = (String) map.get("token");
//                resultInfo.setErrorMsg(token);
//            }
//            resultInfo.setData(count);
//            if (count < 1) {
//                resultInfo.setFlag(false);
//            }
            boolean userIsFavorite = (boolean) map.get("userIsFavorite");
            resultInfo.setFlag(userIsFavorite);
            resultInfo.setData(map);
        }

        return resultInfo;
    }

    public ResultInfo findMyFavorite(HttpServletRequest request, HttpServletResponse response){
        ResultInfo resultInfo = new ResultInfo(true);
        Long currentPage = Long.valueOf(request.getParameter("currentPage"));
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        try {
            PageBean<Favorite> pageBean = favoriteService.findMyFavorite(currentPage,user);
            resultInfo.setData(pageBean);
        } catch (Exception e) {
            resultInfo.setFlag(false);
            e.printStackTrace();
        }

        return resultInfo;
    }
}
