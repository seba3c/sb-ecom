package com.ecommerce.project.service;

import com.ecommerce.project.dto.CartDTO;
import com.ecommerce.project.dto.CartResponse;

public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantity);

    CartDTO updateProductQuantity(Long productId, Integer quantity);

    CartDTO removeProductFromCart(Long productId);

    CartDTO getCart();

    CartResponse getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
