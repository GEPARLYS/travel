package com.tourismwebsite.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author j
 * @Date 2023/8/25 16:00
 * @Version 1.0
 */
public class CookieUtils {
    public static String getCookieValByKey(String key, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String value = null;
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())){
                    value = cookie.getValue();
                }
            }
        }
        return value;
    }
}
