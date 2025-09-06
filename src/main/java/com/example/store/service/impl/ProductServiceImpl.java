package com.example.store.service.impl;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.exception.ProductNotFoundException;
import com.example.store.exception.ValidationException;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;
import com.example.store.service.ProductService;
import com.example.store.service.ValidationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProductService following SOLID principles. Single Responsibility: Manages product business logic.
 * Open/Closed: Extensible through interface. Dependency Inversion: Depends on abstractions (ValidationService,
 * ProductRepository).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ValidationService validationService;

    @Override
    public List<ProductDTO> getAllProducts() {
        log.debug("Retrieving all products");
        try {
            List<Product> products = productRepository.findAll();
            log.debug("Found {} products", products.size());
            return productMapper.productsToProductDTOs(products);
        } catch (Exception e) {
            log.error("Error retrieving all products", e);
            throw new RuntimeException("Failed to retrieve products", e);
        }
    }

    @Override
    @Transactional
    public ProductDTO createProduct(Product product) {
        log.debug("Creating new product: {}", product);

        // Validate input
        validationService.validateProductDescription(product.getDescription());

        try {
            // Sanitize input
            product.setDescription(validationService.sanitizeDescription(product.getDescription()));

            Product savedProduct = productRepository.save(product);
            log.info("Successfully created product with ID: {}", savedProduct.getId());

            return productMapper.productToProductDTO(savedProduct);
        } catch (Exception e) {
            log.error("Error creating product: {}", product, e);
            throw new RuntimeException("Failed to create product", e);
        }
    }

    @Override
    public ProductDTO getProductById(Long id) {
        log.debug("Retrieving product with ID: {}", id);

        validationService.validateProductId(id);

        try {
            Optional<Product> product = productRepository.findById(id);
            if (product.isEmpty()) {
                log.warn("Product not found with ID: {}", id);
                throw ProductNotFoundException.withId(id);
            }

            log.debug("Successfully retrieved product with ID: {}", id);
            return productMapper.productToProductDTO(product.get());
        } catch (ProductNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving product with ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve product", e);
        }
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, Product product) {
        log.debug("Updating product with ID: {}", id);

        validationService.validateProductId(id);
        validationService.validateProductDescription(product.getDescription());

        try {
            Optional<Product> existingProduct = productRepository.findById(id);
            if (existingProduct.isEmpty()) {
                log.warn("Product not found for update with ID: {}", id);
                throw ProductNotFoundException.withId(id);
            }

            Product productToUpdate = existingProduct.get();
            productToUpdate.setDescription(validationService.sanitizeDescription(product.getDescription()));

            Product updatedProduct = productRepository.save(productToUpdate);
            log.info("Successfully updated product with ID: {}", id);

            return productMapper.productToProductDTO(updatedProduct);
        } catch (ProductNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating product with ID: {}", id, e);
            throw new RuntimeException("Failed to update product", e);
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Deleting product with ID: {}", id);

        validationService.validateProductId(id);

        try {
            if (!productRepository.existsById(id)) {
                log.warn("Product not found for deletion with ID: {}", id);
                throw ProductNotFoundException.withId(id);
            }

            productRepository.deleteById(id);
            log.info("Successfully deleted product with ID: {}", id);
        } catch (ProductNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting product with ID: {}", id, e);
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    @Override
    public List<ProductDTO> searchProductsByDescription(String query) {
        log.debug("Searching products with query: {}", query);

        try {
            // Validate search query
            validationService.validateSearchQuery(query);

            List<Product> products;
            if (query == null || query.trim().isEmpty()) {
                log.debug("Empty query, returning all products");
                products = productRepository.findAll();
            } else {
                String sanitizedQuery = query.trim();
                products = productRepository.findByDescriptionContainingIgnoreCase(sanitizedQuery);
                log.debug("Found {} products matching query: {}", products.size(), sanitizedQuery);
            }

            return productMapper.productsToProductDTOs(products);
        } catch (ValidationException e) {
            log.warn("Validation error in search: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error searching products with query: {}", query, e);
            throw new RuntimeException("Failed to search products", e);
        }
    }

    @Override
    public List<ProductDTO> getProductsWithOrders() {
        log.debug("Retrieving products with orders");
        try {
            List<Product> products = productRepository.findProductsWithOrders();
            log.debug("Found {} products with orders", products.size());
            return productMapper.productsToProductDTOs(products);
        } catch (Exception e) {
            log.error("Error retrieving products with orders", e);
            throw new RuntimeException("Failed to retrieve products with orders", e);
        }
    }

    @Override
    public List<ProductDTO> getProductsWithoutOrders() {
        log.debug("Retrieving products without orders");
        try {
            List<Product> products = productRepository.findProductsWithoutOrders();
            log.debug("Found {} products without orders", products.size());
            return productMapper.productsToProductDTOs(products);
        } catch (Exception e) {
            log.error("Error retrieving products without orders", e);
            throw new RuntimeException("Failed to retrieve products without orders", e);
        }
    }
}
