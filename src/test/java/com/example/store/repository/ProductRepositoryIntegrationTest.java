package com.example.store.repository;

import com.example.store.entity.Order;
import com.example.store.entity.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The type Product repository integration test.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductRepository Integration Tests")
class ProductRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;
    private Product product3;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        // Clear the database before each test
        productRepository.deleteAll();

        // Create test products
        product1 = new Product();
        product1.setDescription("Laptop Computer");

        product2 = new Product();
        product2.setDescription("Smartphone Device");

        product3 = new Product();
        product3.setDescription("Tablet Computer");

        // Save products to database
        entityManager.persistAndFlush(product1);
        entityManager.persistAndFlush(product2);
        entityManager.persistAndFlush(product3);
        entityManager.clear();
    }

    /**
     * Should find all products.
     */
    @Test
    @DisplayName("Should find all products")
    void shouldFindAllProducts() {
        // When
        List<Product> products = productRepository.findAll();

        // Then
        assertNotNull(products);
        assertEquals(3, products.size());
        assertTrue(products.stream().anyMatch(p -> "Laptop Computer".equals(p.getDescription())));
        assertTrue(products.stream().anyMatch(p -> "Smartphone Device".equals(p.getDescription())));
        assertTrue(products.stream().anyMatch(p -> "Tablet Computer".equals(p.getDescription())));
    }

    /**
     * Should find product by id.
     */
    @Test
    @DisplayName("Should find product by ID")
    void shouldFindProductById() {
        // When
        var product = productRepository.findById(product1.getId());

        // Then
        assertTrue(product.isPresent());
        assertEquals("Laptop Computer", product.get().getDescription());
        assertEquals(product1.getId(), product.get().getId());
    }

    /**
     * Should return empty when product not found by id.
     */
    @Test
    @DisplayName("Should return empty when product not found by ID")
    void shouldReturnEmptyWhenProductNotFoundById() {
        // When
        var product = productRepository.findById(999L);

        // Then
        assertFalse(product.isPresent());
    }

    /**
     * Should save new product.
     */
    @Test
    @DisplayName("Should save new product")
    void shouldSaveNewProduct() {
        // Given
        Product newProduct = new Product();
        newProduct.setDescription("Desktop Computer");

        // When
        Product savedProduct = productRepository.save(newProduct);

        // Then
        assertNotNull(savedProduct.getId());
        assertEquals("Desktop Computer", savedProduct.getDescription());

        // Verify it's actually saved in database
        var foundProduct = productRepository.findById(savedProduct.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals("Desktop Computer", foundProduct.get().getDescription());
    }

    /**
     * Should update existing product.
     */
    @Test
    @DisplayName("Should update existing product")
    void shouldUpdateExistingProduct() {
        // Given
        product1.setDescription("Updated Laptop");

        // When
        Product updatedProduct = productRepository.save(product1);

        // Then
        assertEquals("Updated Laptop", updatedProduct.getDescription());
        assertEquals(product1.getId(), updatedProduct.getId());

        // Verify it's actually updated in database
        var foundProduct = productRepository.findById(product1.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals("Updated Laptop", foundProduct.get().getDescription());
    }


    /**
     * Should check if product exists by id.
     */
    @Test
    @DisplayName("Should check if product exists by ID")
    void shouldCheckIfProductExistsById() {
        // When & Then
        assertTrue(productRepository.existsById(product1.getId()));
        assertTrue(productRepository.existsById(product2.getId()));
        assertTrue(productRepository.existsById(product3.getId()));
        assertFalse(productRepository.existsById(999L));
    }

    /**
     * Should find products by description containing substring case insensitive.
     */
    @Test
    @DisplayName("Should find products by description containing substring - case insensitive")
    void shouldFindProductsByDescriptionContainingSubstringCaseInsensitive() {
        // When
        List<Product> computerProducts = productRepository.findByDescriptionContainingIgnoreCase("computer");
        List<Product> deviceProducts = productRepository.findByDescriptionContainingIgnoreCase("device");
        List<Product> tabletProducts = productRepository.findByDescriptionContainingIgnoreCase("tablet");
        List<Product> nonExistentProducts = productRepository.findByDescriptionContainingIgnoreCase("nonexistent");

        // Then
        assertEquals(2, computerProducts.size()); // "Laptop Computer" and "Tablet Computer"
        assertTrue(computerProducts.stream().anyMatch(p -> "Laptop Computer".equals(p.getDescription())));
        assertTrue(computerProducts.stream().anyMatch(p -> "Tablet Computer".equals(p.getDescription())));

        assertEquals(1, deviceProducts.size()); // "Smartphone Device"
        assertEquals("Smartphone Device", deviceProducts.get(0).getDescription());

        assertEquals(1, tabletProducts.size()); // "Tablet Computer"
        assertEquals("Tablet Computer", tabletProducts.get(0).getDescription());

        assertTrue(nonExistentProducts.isEmpty());
    }

    /**
     * Should find products by description containing substring partial match.
     */
    @Test
    @DisplayName("Should find products by description containing substring - partial match")
    void shouldFindProductsByDescriptionContainingSubstringPartialMatch() {
        // When
        List<Product> laptopProducts = productRepository.findByDescriptionContainingIgnoreCase("laptop");
        List<Product> smartphoneProducts = productRepository.findByDescriptionContainingIgnoreCase("smartphone");
        List<Product> tabletProducts = productRepository.findByDescriptionContainingIgnoreCase("tablet");

        // Then
        assertEquals(1, laptopProducts.size());
        assertEquals("Laptop Computer", laptopProducts.get(0).getDescription());

        assertEquals(1, smartphoneProducts.size());
        assertEquals("Smartphone Device", smartphoneProducts.get(0).getDescription());

        assertEquals(1, tabletProducts.size());
        assertEquals("Tablet Computer", tabletProducts.get(0).getDescription());
    }

    /**
     * Should find products by description containing substring case variations.
     */
    @Test
    @DisplayName("Should find products by description containing substring - case variations")
    void shouldFindProductsByDescriptionContainingSubstringCaseVariations() {
        // When
        List<Product> upperCaseProducts = productRepository.findByDescriptionContainingIgnoreCase("COMPUTER");
        List<Product> lowerCaseProducts = productRepository.findByDescriptionContainingIgnoreCase("computer");
        List<Product> mixedCaseProducts = productRepository.findByDescriptionContainingIgnoreCase("CoMpUtEr");

        // Then
        assertEquals(2, upperCaseProducts.size());
        assertEquals(2, lowerCaseProducts.size());
        assertEquals(2, mixedCaseProducts.size());

        // All should return the same products
        assertTrue(upperCaseProducts.stream().anyMatch(p -> "Laptop Computer".equals(p.getDescription())));
        assertTrue(upperCaseProducts.stream().anyMatch(p -> "Tablet Computer".equals(p.getDescription())));
    }

    /**
     * Should handle empty search query gracefully.
     */
    @Test
    @DisplayName("Should handle empty search query gracefully")
    void shouldHandleEmptySearchQueryGracefully() {
        // When
        List<Product> emptyQueryProducts = productRepository.findByDescriptionContainingIgnoreCase("");
        List<Product> whitespaceQueryProducts = productRepository.findByDescriptionContainingIgnoreCase("   ");

        // Then
        // In H2, LIKE '%%' matches all records, so empty string returns all products
        assertEquals(3, emptyQueryProducts.size()); // Should return all products
        assertEquals(0, whitespaceQueryProducts.size()); // Whitespace should return 0 products
    }

    /**
     * Should find products with orders.
     */
    @Test
    @DisplayName("Should find products with orders")
    void shouldFindProductsWithOrders() {
        // Given
        Order order = new Order();
        order.setDescription("Test Order");
        order.getProducts().add(product1);
        order.getProducts().add(product2);
        entityManager.persistAndFlush(order);
        entityManager.clear();

        // When
        List<Product> productsWithOrders = productRepository.findProductsWithOrders();

        // Then
        assertEquals(2, productsWithOrders.size());
        assertTrue(productsWithOrders.stream().anyMatch(p -> "Laptop Computer".equals(p.getDescription())));
        assertTrue(productsWithOrders.stream().anyMatch(p -> "Smartphone Device".equals(p.getDescription())));
    }

    /**
     * Should find products without orders.
     */
    @Test
    @DisplayName("Should find products without orders")
    void shouldFindProductsWithoutOrders() {
        // Given
        Order order = new Order();
        order.setDescription("Test Order");
        order.getProducts().add(product1);
        entityManager.persistAndFlush(order);
        entityManager.clear();

        // When
        List<Product> productsWithoutOrders = productRepository.findProductsWithoutOrders();

        // Then
        assertEquals(2, productsWithoutOrders.size());
        assertTrue(productsWithoutOrders.stream().anyMatch(p -> "Smartphone Device".equals(p.getDescription())));
        assertTrue(productsWithoutOrders.stream().anyMatch(p -> "Tablet Computer".equals(p.getDescription())));
    }

    /**
     * Should count total products.
     */
    @Test
    @DisplayName("Should count total products")
    void shouldCountTotalProducts() {
        // When
        long count = productRepository.count();

        // Then
        assertEquals(3, count);
    }

}
