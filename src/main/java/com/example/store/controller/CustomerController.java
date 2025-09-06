package com.example.store.controller;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.service.CustomerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management operations")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve a list of all customers")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved customers",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CustomerDTO.class)))
            })
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
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

    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update an existing customer")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Customer updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CustomerDTO.class))),
                @ApiResponse(responseCode = "404", description = "Customer not found"),
                @ApiResponse(responseCode = "400", description = "Invalid input data")
            })
    public CustomerDTO updateCustomer(
            @Parameter(description = "Customer ID", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody Customer customer) {
        return customerService.updateCustomer(id, customer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Delete a customer by ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Customer not found"),
                @ApiResponse(responseCode = "400", description = "Invalid customer ID")
            })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID", required = true, example = "1") @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search customers by name",
            description = "Search for customers whose name contains the specified query string (case-insensitive)")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved matching customers",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CustomerDTO.class))),
                @ApiResponse(responseCode = "400", description = "Invalid search query")
            })
    public List<CustomerDTO> searchCustomers(
            @Parameter(
                            description = "Search query string to match against customer names",
                            required = false,
                            example = "john")
                    @RequestParam(value = "q", required = false)
                    String query) {
        return customerService.searchCustomersByName(query);
    }
}
