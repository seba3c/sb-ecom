package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConfig;
import com.ecommerce.project.dto.CategoryDetailResponse;
import com.ecommerce.project.dto.CategoryListResponse;
import com.ecommerce.project.dto.CreateCategoryRequest;
import com.ecommerce.project.dto.UpdateCategoryRequest;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryListResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConfig.Pagination.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConfig.Pagination.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConfig.Pagination.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConfig.Pagination.SORT_CATEGORIES_DIR, required = false) String sortOrder
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder));
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDetailResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<CategoryDetailResponse> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }

    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<CategoryDetailResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

}
