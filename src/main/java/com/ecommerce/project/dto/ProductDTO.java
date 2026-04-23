package com.ecommerce.project.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    @NotNull
    @Min(0)
    private Integer quantity;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal discount;
    private CategoryDTO category;
}
