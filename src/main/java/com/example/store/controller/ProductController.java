package com.example.store.controller;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.service.ProductService;

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

/** Product controller. */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product management operations")
public class ProductController {

    private final ProductService productService;

    /**
     * Gets all products paged.
     *
     * @param page the page
     * @param size the size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @return the all products paged
     */
    @GetMapping("")
    @Operation(
            summary = "Get all products with pagination",
            description = "Retrieve a paginated list of all products with their associated order IDs")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved products",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = com.example.store.dto.PagedResponse.class)))
            })
    public com.example.store.dto.PagedResponse<ProductDTO> getAllProductsPaged(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc")
                    String sortOrder) {
        return productService.getAllProducts(page, size, sortBy, sortOrder);
    }

    /**
     * Create product product dto.
     *
     * @param product the product
     * @return the product dto
     */
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

    /**
     * Gets product by id.
     *
     * @param id the id
     * @return the product by id
     */
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

    /**
     * Search products paged com . example . store . dto . paged response.
     *
     * @param query the query
     * @param page the page
     * @param size the size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @return the com . example . store . dto . paged response
     */
    @GetMapping("/search")
    @Operation(
            summary = "Search products by description with pagination",
            description =
                    "Search for products whose description contains the specified query string with pagination support")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved matching products",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = com.example.store.dto.PagedResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid search query")
            })
    public com.example.store.dto.PagedResponse<ProductDTO> searchProductsPaged(
            @Parameter(
                            description = "Search query string to match against product descriptions",
                            required = false,
                            example = "laptop")
                    @RequestParam(value = "q", required = false)
                    String query,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc")
                    String sortOrder) {
        return productService.searchProductsByDescription(query, page, size, sortBy, sortOrder);
    }
}
