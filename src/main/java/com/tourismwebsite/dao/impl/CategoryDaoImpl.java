package com.tourismwebsite.dao.impl;

import com.tourismwebsite.dao.CategoryDao;
import com.tourismwebsite.domain.Category;
import com.tourismwebsite.utils.JDBCUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @Author j
 * @Date 2023/8/25 18:32
 * @Version 1.0
 */
public class CategoryDaoImpl implements CategoryDao {
    private JdbcTemplate template = new JdbcTemplate(JDBCUtil.getDataSource());
    @Override
    public List<Category> findAllCategory() {
        String sql = "select * from tab_category";

        List<Category> categoryList = null;
        try {
            categoryList = template.query(sql, new BeanPropertyRowMapper<>(Category.class));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return categoryList;
    }
}
