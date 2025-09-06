package com.example.store.controller;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.service.CustomerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management operations")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/all")
    @Operation(
            summary = "Get all customers with pagination",
            description = "Retrieve a paginated list of all customers")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved customers",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = com.example.store.dto.PagedResponse.class)))
            })
    public com.example.store.dto.PagedResponse<CustomerDTO> getAllCustomersPaged(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc")
                    String sortOrder) {
        return customerService.getAllCustomers(page, size, sortBy, sortOrder);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new customer", description = "Create a new customer with the provided information")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Customer created successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CustomerDTO.class))),
                @ApiResponse(responseCode = "400", description = "Invalid customer data")
            })
    public CustomerDTO createCustomer(@Valid @RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve a specific customer by their ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Customer found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CustomerDTO.class))),
                @ApiResponse(responseCode = "404", description = "Customer not found"),
                @ApiResponse(responseCode = "400", description = "Invalid customer ID")
            })
    public CustomerDTO getCustomerById(
            @Parameter(description = "Customer ID", required = true, example = "1") @PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search customers by name with pagination",
            description = "Search for customers whose name contains the specified query string with pagination support")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved matching customers",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = com.example.store.dto.PagedResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid search query")
            })
    public com.example.store.dto.PagedResponse<CustomerDTO> searchCustomers(
            @Parameter(
                            description = "Search query string to match against customer names",
                            required = false,
                            example = "john")
                    @RequestParam(value = "q", required = false)
                    String query,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc")
                    String sortOrder) {
        return customerService.searchCustomersByName(query, page, size, sortBy, sortOrder);
    }
}
