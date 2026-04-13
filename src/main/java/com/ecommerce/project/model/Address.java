package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity(name = "addresses")
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Street line 1 must not be blank")
    @Size(min = 5, message = "Street line 1 must have at least 5 characters")
    @Column(name = "street_line_1")
    private String streetLine1;

    @Column(name = "street_line_2")
    private String streetLine2;

    @NotBlank(message = "City must not be blank")
    @Size(min = 5, message = "City must have at least 5 characters")
    private String city;

    @NotBlank(message = "State must not be blank")
    @Size(min = 5, message = "State must have at least 5 characters")
    private String state;

    @NotBlank(message = "Country must not be blank")
    @Size(min = 2, message = "Country must have at least 2 characters")
    private String country;

    @NotBlank(message = "Zipcode must not be blank")
    private String zipcode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
