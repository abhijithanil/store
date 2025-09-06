package com.example.store.service.impl;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.exception.ProductNotFoundException;
import com.example.store.exception.ValidationException;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;
import com.example.store.service.ValidationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductServiceImpl following SOLID principles. Tests all business logic scenarios and corner cases.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setDescription("Laptop Computer");

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setDescription("Laptop Computer");
        productDTO.setOrderIds(Arrays.asList(1L, 2L));
    }

    @Test
    @DisplayName("Should retrieve all products successfully")
    void shouldRetrieveAllProductsSuccessfully() {
        // Given
        List<Product> products = Arrays.asList(product);
        List<ProductDTO> productDTOs = Arrays.asList(productDTO);
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.productsToProductDTOs(products)).thenReturn(productDTOs);

        // When
        List<ProductDTO> result = productService.getAllProducts();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productDTO, result.get(0));
        verify(productRepository).findAll();
        verify(productMapper).productsToProductDTOs(products);
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        // Given
        Product productToCreate = new Product();
        productToCreate.setDescription("Smartphone");
        Product savedProduct = new Product();
        savedProduct.setId(2L);
        savedProduct.setDescription("Smartphone");
        ProductDTO savedProductDTO = new ProductDTO();
        savedProductDTO.setId(2L);
        savedProductDTO.setDescription("Smartphone");

        doNothing().when(validationService).validateProductDescription("Smartphone");
        when(validationService.sanitizeDescription("Smartphone")).thenReturn("Smartphone");
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.productToProductDTO(savedProduct)).thenReturn(savedProductDTO);

        // When
        ProductDTO result = productService.createProduct(productToCreate);

        // Then
        assertNotNull(result);
        assertEquals(savedProductDTO, result);
        verify(validationService).validateProductDescription("Smartphone");
        verify(validationService).sanitizeDescription("Smartphone");
        verify(productRepository).save(productToCreate);
        verify(productMapper).productToProductDTO(savedProduct);
    }

    @Test
    @DisplayName("Should throw ValidationException when creating product with invalid description")
    void shouldThrowValidationExceptionWhenCreatingProductWithInvalidDescription() {
        // Given
        Product productToCreate = new Product();
        productToCreate.setDescription("");

        doThrow(new ValidationException("Required field 'description' is missing or empty"))
                .when(validationService)
                .validateProductDescription("");

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> productService.createProduct(productToCreate));
        assertEquals("Required field 'description' is missing or empty", exception.getMessage());
        verify(validationService).validateProductDescription("");
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should search products with valid query")
    void shouldSearchProductsWithValidQuery() {
        // Given
        String query = "laptop";
        List<Product> products = Arrays.asList(product);
        List<ProductDTO> productDTOs = Arrays.asList(productDTO);

        doNothing().when(validationService).validateSearchQuery(query);
        when(productRepository.findByDescriptionContainingIgnoreCase(query)).thenReturn(products);
        when(productMapper.productsToProductDTOs(products)).thenReturn(productDTOs);

        // When
        List<ProductDTO> result = productService.searchProductsByDescription(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productDTO, result.get(0));
        verify(validationService).validateSearchQuery(query);
        verify(productRepository).findByDescriptionContainingIgnoreCase(query);
        verify(productMapper).productsToProductDTOs(products);
    }

    @Test
    @DisplayName("Should return all products when search query is null")
    void shouldReturnAllProductsWhenSearchQueryIsNull() {
        // Given
        String query = null;
        List<Product> products = Arrays.asList(product);
        List<ProductDTO> productDTOs = Arrays.asList(productDTO);

        doNothing().when(validationService).validateSearchQuery(query);
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.productsToProductDTOs(products)).thenReturn(productDTOs);

        // When
        List<ProductDTO> result = productService.searchProductsByDescription(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(validationService).validateSearchQuery(query);
        verify(productRepository).findAll();
        verify(productRepository, never()).findByDescriptionContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("Should return all products when search query is empty")
    void shouldReturnAllProductsWhenSearchQueryIsEmpty() {
        // Given
        String query = "";
        List<Product> products = Arrays.asList(product);
        List<ProductDTO> productDTOs = Arrays.asList(productDTO);

        doNothing().when(validationService).validateSearchQuery(query);
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.productsToProductDTOs(products)).thenReturn(productDTOs);

        // When
        List<ProductDTO> result = productService.searchProductsByDescription(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(validationService).validateSearchQuery(query);
        verify(productRepository).findAll();
        verify(productRepository, never()).findByDescriptionContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("Should throw ValidationException when search query is invalid")
    void shouldThrowValidationExceptionWhenSearchQueryIsInvalid() {
        // Given
        String invalidQuery = "laptop123";

        doThrow(new ValidationException("Search query contains invalid characters"))
                .when(validationService)
                .validateSearchQuery(invalidQuery);

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> productService.searchProductsByDescription(invalidQuery));
        assertEquals("Search query contains invalid characters", exception.getMessage());
        verify(validationService).validateSearchQuery(invalidQuery);
        verify(productRepository, never()).findAll();
        verify(productRepository, never()).findByDescriptionContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("Should get product by ID successfully")
    void shouldGetProductByIdSuccessfully() {
        // Given
        Long productId = 1L;
        doNothing().when(validationService).validateProductId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        // When
        ProductDTO result = productService.getProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(productDTO, result);
        verify(validationService).validateProductId(productId);
        verify(productRepository).findById(productId);
        verify(productMapper).productToProductDTO(product);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found by ID")
    void shouldThrowProductNotFoundExceptionWhenProductNotFoundById() {
        // Given
        Long productId = 999L;
        doNothing().when(validationService).validateProductId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        ProductNotFoundException exception =
                assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
        assertEquals("Product not found with ID: 999", exception.getMessage());
        verify(validationService).validateProductId(productId);
        verify(productRepository).findById(productId);
        verify(productMapper, never()).productToProductDTO(any());
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        // Given
        Long productId = 1L;
        Product updatedProduct = new Product();
        updatedProduct.setDescription("Updated Laptop");
        Product savedProduct = new Product();
        savedProduct.setId(productId);
        savedProduct.setDescription("Updated Laptop");
        ProductDTO savedProductDTO = new ProductDTO();
        savedProductDTO.setId(productId);
        savedProductDTO.setDescription("Updated Laptop");

        doNothing().when(validationService).validateProductId(productId);
        doNothing().when(validationService).validateProductDescription("Updated Laptop");
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(validationService.sanitizeDescription("Updated Laptop")).thenReturn("Updated Laptop");
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.productToProductDTO(savedProduct)).thenReturn(savedProductDTO);

        // When
        ProductDTO result = productService.updateProduct(productId, updatedProduct);

        // Then
        assertNotNull(result);
        assertEquals(savedProductDTO, result);
        verify(validationService).validateProductId(productId);
        verify(validationService).validateProductDescription("Updated Laptop");
        verify(productRepository).findById(productId);
        verify(validationService).sanitizeDescription("Updated Laptop");
        verify(productRepository).save(any(Product.class));
        verify(productMapper).productToProductDTO(savedProduct);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating non-existent product")
    void shouldThrowProductNotFoundExceptionWhenUpdatingNonExistentProduct() {
        // Given
        Long productId = 999L;
        Product updatedProduct = new Product();
        updatedProduct.setDescription("Updated Laptop");

        doNothing().when(validationService).validateProductId(productId);
        doNothing().when(validationService).validateProductDescription("Updated Laptop");
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class, () -> productService.updateProduct(productId, updatedProduct));
        assertEquals("Product not found with ID: 999", exception.getMessage());
        verify(validationService).validateProductId(productId);
        verify(validationService).validateProductDescription("Updated Laptop");
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {
        // Given
        Long productId = 1L;
        doNothing().when(validationService).validateProductId(productId);
        when(productRepository.existsById(productId)).thenReturn(true);

        // When
        assertDoesNotThrow(() -> productService.deleteProduct(productId));

        // Then
        verify(validationService).validateProductId(productId);
        verify(productRepository).existsById(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when deleting non-existent product")
    void shouldThrowProductNotFoundExceptionWhenDeletingNonExistentProduct() {
        // Given
        Long productId = 999L;
        doNothing().when(validationService).validateProductId(productId);
        when(productRepository.existsById(productId)).thenReturn(false);

        // When & Then
        ProductNotFoundException exception =
                assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId));
        assertEquals("Product not found with ID: 999", exception.getMessage());
        verify(validationService).validateProductId(productId);
        verify(productRepository).existsById(productId);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should get products with orders successfully")
    void shouldGetProductsWithOrdersSuccessfully() {
        // Given
        List<Product> products = Arrays.asList(product);
        List<ProductDTO> productDTOs = Arrays.asList(productDTO);
        when(productRepository.findProductsWithOrders()).thenReturn(products);
        when(productMapper.productsToProductDTOs(products)).thenReturn(productDTOs);

        // When
        List<ProductDTO> result = productService.getProductsWithOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productDTO, result.get(0));
        verify(productRepository).findProductsWithOrders();
        verify(productMapper).productsToProductDTOs(products);
    }

    @Test
    @DisplayName("Should get products without orders successfully")
    void shouldGetProductsWithoutOrdersSuccessfully() {
        // Given
        List<Product> products = Arrays.asList(product);
        List<ProductDTO> productDTOs = Arrays.asList(productDTO);
        when(productRepository.findProductsWithoutOrders()).thenReturn(products);
        when(productMapper.productsToProductDTOs(products)).thenReturn(productDTOs);

        // When
        List<ProductDTO> result = productService.getProductsWithoutOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productDTO, result.get(0));
        verify(productRepository).findProductsWithoutOrders();
        verify(productMapper).productsToProductDTOs(products);
    }

    @Test
    @DisplayName("Should handle repository exception gracefully")
    void shouldHandleRepositoryExceptionGracefully() {
        // Given
        when(productRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.getAllProducts());
        assertEquals("Failed to retrieve products", exception.getMessage());
        verify(productRepository).findAll();
    }
}
