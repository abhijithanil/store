package com.example.store.controller;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProductController using mocked services. Tests complete request-response cycle.
 */
@WebMvcTest(ProductController.class)
@DisplayName("ProductController Integration Tests")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        reset(productService);
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() throws Exception {
        // Given
        Product product = new Product();
        product.setDescription("Laptop Computer");
        
        ProductDTO savedProduct = new ProductDTO();
        savedProduct.setId(1L);
        savedProduct.setDescription("Laptop Computer");
        
        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Laptop Computer"));

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should return validation error for empty product description")
    void shouldReturnValidationErrorForEmptyProductDescription() throws Exception {
        // Given
        Product product = new Product();
        product.setDescription("");

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.description").value("Product description is required"));

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should return validation error for null product description")
    void shouldReturnValidationErrorForNullProductDescription() throws Exception {
        // Given
        Product product = new Product();
        product.setDescription(null);

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.description").value("Product description is required"));

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should return validation error for product description too long")
    void shouldReturnValidationErrorForProductDescriptionTooLong() throws Exception {
        // Given
        Product product = new Product();
        product.setDescription("a".repeat(501)); // Exceeds 500 character limit

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.description")
                        .value("Product description must be between 1 and 500 characters"));

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should get all products")
    void shouldGetAllProducts() throws Exception {
        // Given
        ProductDTO product1 = new ProductDTO();
        product1.setId(1L);
        product1.setDescription("Laptop Computer");
        
        ProductDTO product2 = new ProductDTO();
        product2.setId(2L);
        product2.setDescription("Smartphone Device");
        
        List<ProductDTO> products = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("Laptop Computer", "Smartphone Device")));

        verify(productService).getAllProducts();
    }

    @Test
    @DisplayName("Should get product by ID")
    void shouldGetProductById() throws Exception {
        // Given
        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setDescription("Laptop Computer");
        
        when(productService.getProductById(1L)).thenReturn(product);

        // When & Then
        mockMvc.perform(get("/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Laptop Computer"));

        verify(productService).getProductById(1L);
    }

    @Test
    @DisplayName("Should return 404 when product not found by ID")
    void shouldReturn404WhenProductNotFoundById() throws Exception {
        // Given
        when(productService.getProductById(999L))
                .thenThrow(new com.example.store.exception.ProductNotFoundException("Product not found with ID: 999"));

        // When & Then
        mockMvc.perform(get("/products/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Product Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found with ID: 999"));

        verify(productService).getProductById(999L);
    }

    @Test
    @DisplayName("Should return 400 for invalid product ID")
    void shouldReturn400ForInvalidProductId() throws Exception {
        // Given
        when(productService.getProductById(-1L))
                .thenThrow(new com.example.store.exception.ValidationException("Product ID must be positive"));

        // When & Then
        mockMvc.perform(get("/products/{id}", -1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Product ID must be positive"));

        verify(productService).getProductById(-1L);
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() throws Exception {
        // Given
        Product updatedProduct = new Product();
        updatedProduct.setDescription("Updated Laptop");
        
        ProductDTO savedProduct = new ProductDTO();
        savedProduct.setId(1L);
        savedProduct.setDescription("Updated Laptop");
        
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(put("/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Updated Laptop"));

        verify(productService).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent product")
    void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
        // Given
        Product updatedProduct = new Product();
        updatedProduct.setDescription("Updated Laptop");
        
        when(productService.updateProduct(eq(999L), any(Product.class)))
                .thenThrow(new com.example.store.exception.ProductNotFoundException("Product not found with ID: 999"));

        // When & Then
        mockMvc.perform(put("/products/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Product Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found with ID: 999"));

        verify(productService).updateProduct(eq(999L), any(Product.class));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/products/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent product")
    void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {
        // Given
        doThrow(new com.example.store.exception.ProductNotFoundException("Product not found with ID: 999"))
                .when(productService).deleteProduct(999L);

        // When & Then
        mockMvc.perform(delete("/products/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Product Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found with ID: 999"));

        verify(productService).deleteProduct(999L);
    }

    @Test
    @DisplayName("Should search products by description")
    void shouldSearchProductsByDescription() throws Exception {
        // Given
        ProductDTO product1 = new ProductDTO();
        product1.setId(1L);
        product1.setDescription("Laptop Computer");
        
        ProductDTO product2 = new ProductDTO();
        product2.setId(2L);
        product2.setDescription("Tablet Computer");
        
        List<ProductDTO> products = Arrays.asList(product1, product2);
        when(productService.searchProductsByDescription("computer")).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/products/search").param("q", "computer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("Laptop Computer", "Tablet Computer")));

        verify(productService).searchProductsByDescription("computer");
    }

    @Test
    @DisplayName("Should return all products when search query is empty")
    void shouldReturnAllProductsWhenSearchQueryIsEmpty() throws Exception {
        // Given
        ProductDTO product1 = new ProductDTO();
        product1.setId(1L);
        product1.setDescription("Laptop Computer");
        
        ProductDTO product2 = new ProductDTO();
        product2.setId(2L);
        product2.setDescription("Smartphone Device");
        
        List<ProductDTO> products = Arrays.asList(product1, product2);
        when(productService.searchProductsByDescription("")).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/products/search").param("q", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("Laptop Computer", "Smartphone Device")));

        verify(productService).searchProductsByDescription("");
    }

    @Test
    @DisplayName("Should return empty list when no products match search")
    void shouldReturnEmptyListWhenNoProductsMatchSearch() throws Exception {
        // Given
        when(productService.searchProductsByDescription("nonexistent")).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/products/search").param("q", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService).searchProductsByDescription("nonexistent");
    }

    @Test
    @DisplayName("Should return 400 for invalid search query")
    void shouldReturn400ForInvalidSearchQuery() throws Exception {
        // Given
        when(productService.searchProductsByDescription("laptop123"))
                .thenThrow(new com.example.store.exception.ValidationException("Search query contains invalid characters"));

        // When & Then
        mockMvc.perform(get("/products/search").param("q", "laptop123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Search query contains invalid characters"));

        verify(productService).searchProductsByDescription("laptop123");
    }

    @Test
    @DisplayName("Should handle case-insensitive search")
    void shouldHandleCaseInsensitiveSearch() throws Exception {
        // Given
        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setDescription("Laptop Computer");
        
        when(productService.searchProductsByDescription("LAPTOP")).thenReturn(Arrays.asList(product));
        when(productService.searchProductsByDescription("laptop")).thenReturn(Arrays.asList(product));
        when(productService.searchProductsByDescription("LaPtOp")).thenReturn(Arrays.asList(product));

        // When & Then
        mockMvc.perform(get("/products/search").param("q", "LAPTOP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Laptop Computer"));

        mockMvc.perform(get("/products/search").param("q", "laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Laptop Computer"));

        mockMvc.perform(get("/products/search").param("q", "LaPtOp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Laptop Computer"));

        verify(productService).searchProductsByDescription("LAPTOP");
        verify(productService).searchProductsByDescription("laptop");
        verify(productService).searchProductsByDescription("LaPtOp");
    }

    @Test
    @DisplayName("Should sanitize product descriptions on creation")
    void shouldSanitizeProductDescriptionsOnCreation() throws Exception {
        // Given
        Product product = new Product();
        product.setDescription("  laptop computer  "); // Extra whitespace
        
        ProductDTO savedProduct = new ProductDTO();
        savedProduct.setId(1L);
        savedProduct.setDescription("Laptop Computer"); // Sanitized
        
        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Laptop Computer")); // Should be sanitized

        verify(productService).createProduct(any(Product.class));
    }
}
