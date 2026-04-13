package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AppRole name;
}
