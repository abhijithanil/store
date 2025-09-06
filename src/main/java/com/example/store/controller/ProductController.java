package com.example.store.controller;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.service.ProductService;

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

/**
 * REST controller for product operations following SOLID principles. Single Responsibility: Handles HTTP requests for
 * product management.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product management operations")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Retrieve a list of all products with their associated order IDs")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved products",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProductDTO.class)))
            })
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product", description = "Create a new product with the provided information")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Product created successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProductDTO.class))),
                @ApiResponse(responseCode = "400", description = "Invalid product data")
            })
    public ProductDTO createProduct(@Valid @RequestBody Product product) {
        return productService.createProduct(product);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Retrieve a specific product by its ID with associated order IDs")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Product found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProductDTO.class))),
                @ApiResponse(responseCode = "404", description = "Product not found"),
                @ApiResponse(responseCode = "400", description = "Invalid product ID")
            })
    public ProductDTO getProductById(
            @Parameter(description = "Product ID", required = true, example = "1") @PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Product updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProductDTO.class))),
                @ApiResponse(responseCode = "404", description = "Product not found"),
                @ApiResponse(responseCode = "400", description = "Invalid input data")
            })
    public ProductDTO updateProduct(
            @Parameter(description = "Product ID", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product by ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Product not found"),
                @ApiResponse(responseCode = "400", description = "Invalid product ID")
            })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true, example = "1") @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search products by description",
            description =
                    "Search for products whose description contains the specified query string (case-insensitive)")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved matching products",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProductDTO.class))),
                @ApiResponse(responseCode = "400", description = "Invalid search query")
            })
    public List<ProductDTO> searchProducts(
            @Parameter(
                            description = "Search query string to match against product descriptions",
                            required = false,
                            example = "laptop")
                    @RequestParam(value = "q", required = false)
                    String query) {
        return productService.searchProductsByDescription(query);
    }

    @GetMapping("/with-orders")
    @Operation(summary = "Get products with orders", description = "Retrieve products that are contained in orders")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved products with orders",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProductDTO.class)))
            })
    public List<ProductDTO> getProductsWithOrders() {
        return productService.getProductsWithOrders();
    }

    @GetMapping("/without-orders")
    @Operation(
            summary = "Get products without orders",
            description = "Retrieve products that are not contained in any orders")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved products without orders",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProductDTO.class)))
            })
    public List<ProductDTO> getProductsWithoutOrders() {
        return productService.getProductsWithoutOrders();
    }
}
