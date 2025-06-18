package com.tourismwebsite.web.servlet;

import com.tourismwebsite.constant.Constant;
import com.tourismwebsite.domain.ResultInfo;
import com.tourismwebsite.domain.User;
import com.tourismwebsite.factory.BaseFactory;
import com.tourismwebsite.service.UserService;
import com.tourismwebsite.utils.Md5Util;
import com.tourismwebsite.utils.UuidUtil;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 * @Author j
 * @Date 2023/8/24 17:20
 * @Version 1.0
 */
@WebServlet(name = "UserServlet", urlPatterns = "/user")
public class UserServlet extends BaseServlet {
    private UserService userService = (UserService) BaseFactory.getInterface("userService");
    public ResultInfo register(HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException, IOException {
        String verification_code = request.getParameter("verification_Code");
        HttpSession session = request.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        ResultInfo resultInfo = new ResultInfo();
        boolean flag = false;
        if (verification_code.equalsIgnoreCase(checkcode_server)){
            Map<String, String[]> parameterMap = request.getParameterMap();
            User user = new User();
            try {
                BeanUtils.populate(user,parameterMap);
                user.setStatus(Constant.UNACTIVE);//待激活状态

                user.setCode(UuidUtil.getUuid());

                String password = user.getPassword();

                user.setPassword(Md5Util.encodeByMd5(password));
                flag = userService.register(user);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (flag){
                resultInfo.setFlag(true);
            }else{
                resultInfo.setFlag(false);
                resultInfo.setErrorMsg("register fail,reregister!!");
            }

        }else{
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("verification code error");
        }

        return resultInfo;
    }
    public ResultInfo checkUserName(HttpServletRequest request, HttpServletResponse response){
        ResultInfo resultInfo = new ResultInfo(true);
        String username = request.getParameter("username");

        try {
            User user = userService.findUsername(username);

            if (user != null){
                resultInfo.setData(false); //用户名已存在
            }else{
                resultInfo.setData(true);

            }
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setFlag(false);
        }

        return resultInfo;
    }

    public ResultInfo active(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String code = request.getParameter("code");
        boolean flag = userService.active(code);

        if (flag){
            request.getRequestDispatcher(request.getContextPath()+"/login.html").forward(request,response);
        }else{

            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write("<script>alert('激活失败');</script>");

        }
        return null;
    }

    public ResultInfo login(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ResultInfo resultInfo = new ResultInfo(true);
        HttpSession session = request.getSession();
        String leadingCheckCode = request.getParameter("check");
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");

        try {
            if (leadingCheckCode.equalsIgnoreCase(checkcode_server)){
                Map<String, String[]> parameterMap = request.getParameterMap();
                String username = request.getParameter("username");
                String password = request.getParameter("password");

                password = Md5Util.encodeByMd5(password);

                User findUser = userService.doLogin(username,password);
                if (findUser != null){
                    session.setAttribute("user",findUser);
                    resultInfo.setData(true);
                    if (parameterMap.containsKey("auto_login")){
                        String token = UUID.randomUUID().toString();
                        userService.saveToken(token,findUser);
                        
                        String cookieValue = findUser.getUsername() + "_" + token;
                        cookieValue = URLEncoder.encode(cookieValue,"UTF-8");
                        Cookie cookie = new Cookie("auto_login",cookieValue );
                        cookie.setMaxAge(60 * 60 * 24 * 7);
                        cookie.setPath(request.getContextPath() + "/");
                        response.addCookie(cookie);
                    }else {
                        Cookie cookie = new Cookie("auto_login", null);
                        cookie.setMaxAge(60 * 60 * 24 * 7);
                        cookie.setPath(request.getContextPath() + "/");
                        response.addCookie(cookie);
                    }
                }else{
                    resultInfo.setData(false);
                    resultInfo.setErrorMsg("account number or password error!!1");
                }

            }else{
                resultInfo.setData(false);
                resultInfo.setErrorMsg("checkCode Error!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setData(false);
            resultInfo.setErrorMsg(e.getMessage());
        }

        return resultInfo;
    }
    public ResultInfo getUserInfo(HttpServletRequest request, HttpServletResponse response){
        ResultInfo resultInfo = new ResultInfo(false);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null){
            resultInfo.setFlag(true);
            resultInfo.setData(user);
        }
        return resultInfo;
    }

    public ResultInfo logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        ResultInfo resultInfo = new ResultInfo(true);
        HttpSession session = request.getSession();
        session.invalidate();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().contains("auto_login")){
                cookie.setMaxAge(0);
                cookie.setPath(request.getContextPath() +"/");
                response.addCookie(cookie);
            }
        }
        resultInfo.setData(true);

        return resultInfo;


    }


}

