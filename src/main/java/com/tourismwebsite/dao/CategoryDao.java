package com.tourismwebsite.dao;

import com.tourismwebsite.domain.Category;

import java.util.List;

/**
 * @Author j
 * @Date 2023/8/25 18:32
 * @Version 1.0
 */
public interface CategoryDao {
    List<Category> findAllCategory();
}
