package com.tourismwebsite.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourismwebsite.domain.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @Author j
 * @Date 2023/8/24 15:08
 * @Version 1.0
 */
public class BaseServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        Class<? extends BaseServlet> aClass = this.getClass();
        try {
            Method declaredMethod = aClass.getDeclaredMethod(action, HttpServletRequest.class, HttpServletResponse.class);
            declaredMethod.setAccessible(true);
            ResultInfo resultInfo = (ResultInfo) declaredMethod.invoke(this, request, response);
            if (resultInfo != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonData = objectMapper.writeValueAsString(resultInfo);
//                response.setContentType("text/html;charset=utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(jsonData);
                writer.flush();
                writer.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
