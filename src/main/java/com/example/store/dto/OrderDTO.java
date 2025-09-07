package com.example.store.dto;

import lombok.Data;

import java.util.List;

/** Order dto. */
@Data
public class OrderDTO {
    private Long id;
    private String description;
    private OrderCustomerDTO customer;
    private List<ProductDTO> products;
}
