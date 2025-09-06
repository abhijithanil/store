package com.example.store.controller;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.mapper.ProductMapper;
import com.example.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Unit tests for ProductController following SOLID principles. Tests all HTTP endpoints with mocked dependencies. */
@WebMvcTest(ProductController.class)
@ComponentScan(basePackageClasses = ProductMapper.class)
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setDescription("Laptop Computer");
        product.setId(1L);

        productDTO = new ProductDTO();
        productDTO.setDescription("Laptop Computer");
        productDTO.setId(1L);
        productDTO.setOrderIds(List.of(1L, 2L));
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productService.createProduct(product)).thenReturn(productDTO);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Laptop Computer"));
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Laptop Computer"));
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Laptop Computer"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product updatedProduct = new Product();
        updatedProduct.setDescription("Updated Laptop");
        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setId(1L);
        updatedProductDTO.setDescription("Updated Laptop");

        when(productService.updateProduct(1L, updatedProduct)).thenReturn(updatedProductDTO);

        mockMvc.perform(put("/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated Laptop"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/products/{id}", 1L)).andExpect(status().isNoContent());
    }

    @Test
    void testSearchProducts() throws Exception {
        when(productService.searchProductsByDescription("laptop")).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/products/search").param("q", "laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Laptop Computer"));
    }

    @Test
    void testSearchProductsWithEmptyQuery() throws Exception {
        when(productService.searchProductsByDescription("")).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/products/search").param("q", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Laptop Computer"));
    }

    @Test
    void testSearchProductsWithNoResults() throws Exception {
        when(productService.searchProductsByDescription("nonexistent")).thenReturn(List.of());

        mockMvc.perform(get("/products/search").param("q", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetProductsWithOrders() throws Exception {
        when(productService.getProductsWithOrders()).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/products/with-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Laptop Computer"));
    }

    @Test
    void testGetProductsWithoutOrders() throws Exception {
        when(productService.getProductsWithoutOrders()).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/products/without-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Laptop Computer"));
    }
}
