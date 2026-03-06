package com.ecommerce.project.controller;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<List<Category>> list() {
        return ResponseEntity.ok(categoryService.list());
    }

    @PostMapping("/public/categories")
    public ResponseEntity<String> create(@RequestBody Category category) {
        categoryService.create(category);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category created successfully");
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            String status = categoryService.delete(id);
            return ResponseEntity.ok(status);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }

    }

    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.update(id, category);
            return ResponseEntity.ok("Updated category: " + updatedCategory);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }

    }

}
