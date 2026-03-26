package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConfig;
import com.ecommerce.project.dto.ProductDTO;
import com.ecommerce.project.dto.ProductResponse;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/products")
    public ResponseEntity<ProductDTO> createProduct(
            @PathVariable Long categoryId,
            @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(categoryId, productDTO));
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(productId, productDTO));
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.deleteProduct(productId));
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(defaultValue = AppConfig.Pagination.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConfig.Pagination.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConfig.Pagination.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConfig.Pagination.SORT_PRODUCTS_DIR, required = false) String sortOrder) {
        return ResponseEntity.ok(productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/public/products/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(
            @PathVariable String keyword,
            @RequestParam(defaultValue = AppConfig.Pagination.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConfig.Pagination.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConfig.Pagination.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConfig.Pagination.SORT_PRODUCTS_DIR, required = false) String sortOrder) {
        return ResponseEntity.ok(productService.getProductsByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = AppConfig.Pagination.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConfig.Pagination.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConfig.Pagination.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConfig.Pagination.SORT_PRODUCTS_DIR, required = false) String sortOrder) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder));
    }
}
