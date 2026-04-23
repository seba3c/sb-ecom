package com.ecommerce.project.service;

import com.ecommerce.project.dto.CartDTO;

import java.util.List;

public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantity);

    CartDTO updateProductQuantity(Long productId, Integer quantity);

    CartDTO removeProductFromCart(Long productId);

    CartDTO getCart();

    List<CartDTO> getAllCarts();
}
