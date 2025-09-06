package com.example.store.dto;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.util.List;


/**
 * Create order request.
 */
@Data
public class CreateOrderRequest {

    private String description;

    @NotNull(message = "Customer ID is required") private Long customerId;

    private List<Long> productIds;
}
