package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "products")
@Getter
@Setter
@ToString(exclude = {"category", "seller"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Product name must not be blank")
    @Size(min = 5, message = "Product name must have at least 5 characters")
    @Column(unique = true)
    private String name;

    @NotBlank(message = "Product description must not be blank")
    private String description;

    private int quantity = 0;

    private double price;

    private double discount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

}
