package com.tourismwebsite.web.servlet;

import com.tourismwebsite.domain.ResultInfo;
import com.tourismwebsite.factory.BaseFactory;
import com.tourismwebsite.service.CategoryService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author j
 * @Date 2023/8/24 15:20
 * @Version 1.0
 */
@WebServlet(name = "CategoryServlet", urlPatterns = "/category")
public class CategoryServlet extends BaseServlet {
    private CategoryService categoryService = (CategoryService) BaseFactory.getInterface("categoryService");
    public ResultInfo findAll(HttpServletRequest request, HttpServletResponse response){
        ResultInfo resultInfo = new ResultInfo(true);
        String jsonString = categoryService.findAllCategory();
        resultInfo.setData(jsonString); //todo
        return resultInfo;
    }

}
