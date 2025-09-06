package com.example.store.dto;

import lombok.Data;

import java.util.List;

/**
 * Order DTO following SOLID principles. Single Responsibility: Represents order data transfer object with customer and
 * product information.
 */
@Data
public class OrderDTO {
    private Long id;
    private String description;
    private OrderCustomerDTO customer;
    private List<ProductDTO> products;
}
