package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private List<Category> categories = new ArrayList<Category>();
    private Long nextId = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        category.setId(nextId++);
        categories.add(category);
    }

    @Override
    public String deleteCategory(Long id) {
        boolean removed = categories.removeIf(category -> category.getId().equals(id));
        if (removed) {
            return "Category with id " + id + " deleted successfully";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
    }
}
