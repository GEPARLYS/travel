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
import java.net.URLDecoder;
import java.net.URLEncoder;

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
                    String decode = URLDecoder.decode(auto_loginInfo, "utf-8");
                    String[] parts = decode.split("_",2);
                    if (parts.length == 2){
                        String username = parts[0];
                        String token = parts[1];
                        
                        UserService userService = (UserService) BaseFactory.getInterface("userService");
                        
                        user = userService.findByUsernameAndToken(username,token);
                        if (user != null){

                            try {
                                user = userService.doLogin(user.getUsername(), user.getPassword());
                                if (user != null) {
                                    request.getSession().setAttribute("user", user);
                                    chain.doFilter(request, response);
                                    return;
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        
                      
                    }
                  
                   
                }
             
            }

           
        }
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
