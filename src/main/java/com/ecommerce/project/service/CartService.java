package com.ecommerce.project.service;

import com.ecommerce.project.dto.CartDetailResponse;
import com.ecommerce.project.dto.CartListResponse;

public interface CartService {

    CartDetailResponse addProductToCart(Long productId, Integer quantity);

    CartDetailResponse updateProductQuantity(Long productId, Integer quantity);

    CartDetailResponse removeProductFromCart(Long productId);

    CartDetailResponse getCart();

    CartListResponse getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
