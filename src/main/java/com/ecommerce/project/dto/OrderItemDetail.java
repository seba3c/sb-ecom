package com.ecommerce.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDetail {

    private Long id;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private ProductDetailResponse product;
}
