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
 * @Date 2023/8/24 15:05
 * @Version 1.0
 */
@WebFilter(filterName = "EncodingFilter", urlPatterns = "/*")
public class EncodingFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String method = request.getMethod();

        if ("POST".equals(method)) {
            request.setCharacterEncoding("utf-8");
        }
        //处理响应乱码
        response.setContentType("text/html;charset=utf-8");
        chain.doFilter(request,response);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
