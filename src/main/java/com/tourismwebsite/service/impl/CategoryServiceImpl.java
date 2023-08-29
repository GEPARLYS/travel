package com.tourismwebsite.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourismwebsite.constant.Constant;
import com.tourismwebsite.dao.CategoryDao;
import com.tourismwebsite.domain.Category;
import com.tourismwebsite.factory.BaseFactory;
import com.tourismwebsite.service.CategoryService;
import com.tourismwebsite.utils.JedisUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @Author j
 * @Date 2023/8/25 18:30
 * @Version 1.0
 */
public class CategoryServiceImpl implements CategoryService {
    private CategoryDao categoryDao = (CategoryDao) BaseFactory.getInterface("categoryDao");

    @Override
    public String findAllCategory() {
        String jsonData = getFromRedisData();
        if (jsonData == null) {
            return getFromSqlData();
        }
        return jsonData;
    }

    private String getFromSqlData(){
        List<Category> categoryList = categoryDao.findAllCategory();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(categoryList);
            save2Redis(jsonStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    private String getFromRedisData(){
        Jedis jedis = JedisUtil.getJedis();
        String jsonStr = null;
        try {
            jsonStr = jedis.get(Constant.ALLCATEGORY);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }

        return jsonStr;
    }

    private void save2Redis(String jsonStr){
        Jedis jedis = JedisUtil.getJedis();
        jedis.set(Constant.ALLCATEGORY,jsonStr);
        jedis.close();

    }

}
