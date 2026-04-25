package com.ecommerce.project.service;

import com.ecommerce.project.dto.CategoryDetailResponse;
import com.ecommerce.project.dto.CategoryListResponse;
import com.ecommerce.project.dto.CreateCategoryRequest;
import com.ecommerce.project.dto.UpdateCategoryRequest;

public interface CategoryService {

    CategoryListResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDetailResponse createCategory(CreateCategoryRequest request);

    CategoryDetailResponse deleteCategory(Long id);

    CategoryDetailResponse updateCategory(Long id, UpdateCategoryRequest request);
}
