package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;


import java.util.List;

public interface CategoryService {

    List<Category> list();

    void create(Category category);

    String delete(Long id);

    Category update(Long id, Category category);
}
