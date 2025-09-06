package com.example.store.dto;

import lombok.Data;

import java.util.List;

/**
 * Product DTO following SOLID principles. Single Responsibility: Represents product data transfer object with order
 * information.
 */
@Data
public class ProductDTO {
    private Long id;
    private String description;
    private List<Long> orderIds;
}
