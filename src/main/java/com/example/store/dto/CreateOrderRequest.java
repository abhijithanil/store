package com.example.store.dto;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.util.List;

/**
 * Request DTO for creating orders following SOLID principles. Single Responsibility: Represents order creation data.
 */
@Data
public class CreateOrderRequest {

    private String description;

    @NotNull(message = "Customer ID is required") private Long customerId;

    private List<Long> productIds;
}
