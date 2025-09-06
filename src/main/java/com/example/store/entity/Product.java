package com.example.store.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Product entity following SOLID principles. Single Responsibility: Represents product data structure with proper JPA
 * relationships.
 */
@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product description is required")
    @Size(min = 1, max = 500, message = "Product description must be between 1 and 500 characters")
    @Column(nullable = false)
    private String description;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
}
