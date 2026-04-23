package com.ecommerce.project.controller;

import com.ecommerce.project.dto.CartDTO;
import com.ecommerce.project.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/my_cart/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(
            @PathVariable Long productId,
            @PathVariable Integer quantity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addProductToCart(productId, quantity));
    }

    @PutMapping("/my_cart/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> updateProductQuantity(
            @PathVariable Long productId,
            @PathVariable Integer quantity) {
        return ResponseEntity.ok(cartService.updateProductQuantity(productId, quantity));
    }

    @DeleteMapping("/my_cart/{productId}")
    public ResponseEntity<CartDTO> removeProductFromCart(@PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeProductFromCart(productId));
    }

    @GetMapping("/my_cart")
    public ResponseEntity<CartDTO> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @GetMapping("/admin/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }
}
