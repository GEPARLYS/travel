package com.tourismwebsite.web.filter;

import com.tourismwebsite.domain.User;
import com.tourismwebsite.factory.BaseFactory;
import com.tourismwebsite.service.UserService;
import com.tourismwebsite.utils.CookieUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author j
 * @Date 2023/8/26 9:09
 * @Version 1.0
 */
@WebFilter(filterName = "AutoLoginFilter", urlPatterns = "/*")
public class AutoLoginFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (request.getRequestURI().contains("login")){
            chain.doFilter(req,resp);
            return;
        }else{
            User user = (User) request.getSession().getAttribute("user");
            if (user == null){
                String auto_loginInfo = CookieUtils.getCookieValByKey("auto_login", request);
                if (auto_loginInfo != null && !auto_loginInfo.equals("")) {
                    String username = auto_loginInfo.split("_")[0];
                    String password = auto_loginInfo.split("_")[1];
                    UserService userService = (UserService) BaseFactory.getInterface("userService");

                    try {
                        user = userService.doLogin(username, password);
                        if (user != null) {
                            request.getSession().setAttribute("user", user);
                            chain.doFilter(request, response);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
