package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConfig;
import com.ecommerce.project.dto.CartDetailResponse;
import com.ecommerce.project.dto.CartListResponse;
import com.ecommerce.project.service.CartService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/my_cart/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDetailResponse> addProductToCart(
            @PathVariable Long productId,
            @PathVariable @Min(1) Integer quantity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addProductToCart(productId, quantity));
    }

    @PutMapping("/my_cart/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDetailResponse> updateProductQuantity(
            @PathVariable Long productId,
            @PathVariable @Min(1) Integer quantity) {
        return ResponseEntity.ok(cartService.updateProductQuantity(productId, quantity));
    }

    @DeleteMapping("/my_cart/{productId}")
    public ResponseEntity<CartDetailResponse> removeProductFromCart(@PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeProductFromCart(productId));
    }

    @GetMapping("/my_cart")
    public ResponseEntity<CartDetailResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @GetMapping("/admin/carts")
    public ResponseEntity<CartListResponse> getAllCarts(
            @RequestParam(name = "pageNumber", defaultValue = AppConfig.Pagination.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConfig.Pagination.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConfig.Pagination.SORT_CARTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConfig.Pagination.SORT_CARTS_DIR, required = false) String sortOrder
    ) {
        return ResponseEntity.ok(cartService.getAllCarts(pageNumber, pageSize, sortBy, sortOrder));
    }
}
