package com.example.store.service.impl;

import com.example.store.dto.PagedResponse;
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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * The type Product service.
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
    @Cacheable(value = "pagedProducts", key = "#page + '_' + #size + '_' + #sortBy + '_' + #sortOrder")
    public PagedResponse<ProductDTO> getAllProducts(int page, int size, String sortBy, String sortOrder) {
        log.debug(
                "Retrieving products with pagination - page: {}, size: {}, sortBy: {}, sortOrder: {}",
                page,
                size,
                sortBy,
                sortOrder);
        try {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Product> productPage = productRepository.findAll(pageable);

            log.debug(
                    "Found {} products on page {} of {}",
                    productPage.getContent().size(),
                    page + 1,
                    productPage.getTotalPages());
            return PagedResponse.of(
                    productPage.map(product -> productMapper.productToProductDTO(product)), sortBy, sortOrder);
        } catch (Exception e) {
            log.error("Error retrieving products with pagination", e);
            throw new RuntimeException("Failed to retrieve products", e);
        }
    }

    @Override
    @Cacheable(value = "products", key = "'all'")
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
    @CacheEvict(
            value = {"products", "pagedProducts"},
            allEntries = true)
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
    @Cacheable(value = "products", key = "#id")
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
    @CacheEvict(
            value = {"products", "pagedProducts"},
            allEntries = true)
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
    @CacheEvict(
            value = {"products", "pagedProducts"},
            allEntries = true)
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
    public PagedResponse<ProductDTO> searchProductsByDescription(
            String query, int page, int size, String sortBy, String sortOrder) {
        log.debug(
                "Searching products with pagination - query: {}, page: {}, size: {}, sortBy: {}, sortOrder: {}",
                query,
                page,
                size,
                sortBy,
                sortOrder);

        try {
            // Validate search query
            validationService.validateSearchQuery(query);

            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Product> productPage;
            if (query == null || query.trim().isEmpty()) {
                log.debug("Empty query, returning all products with pagination");
                productPage = productRepository.findAll(pageable);
            } else {
                String sanitizedQuery = query.trim();
                productPage = productRepository.findByDescriptionContainingIgnoreCase(sanitizedQuery, pageable);
                log.debug(
                        "Found {} products matching query: {} on page {} of {}",
                        productPage.getContent().size(),
                        sanitizedQuery,
                        page + 1,
                        productPage.getTotalPages());
            }

            return PagedResponse.of(
                    productPage.map(product -> productMapper.productToProductDTO(product)), sortBy, sortOrder);
        } catch (ValidationException e) {
            log.warn("Validation error in search: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error searching products with pagination - query: {}", query, e);
            throw new RuntimeException("Failed to search products", e);
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
    public PagedResponse<ProductDTO> getProductsWithOrders(int page, int size, String sortBy, String sortOrder) {
        log.debug(
                "Retrieving products with orders with pagination - page: {}, size: {}, sortBy: {}, sortOrder: {}",
                page,
                size,
                sortBy,
                sortOrder);
        try {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Product> productPage = productRepository.findProductsWithOrders(pageable);
            log.debug(
                    "Found {} products with orders on page {} of {}",
                    productPage.getContent().size(),
                    page + 1,
                    productPage.getTotalPages());

            return PagedResponse.of(
                    productPage.map(product -> productMapper.productToProductDTO(product)), sortBy, sortOrder);
        } catch (Exception e) {
            log.error("Error retrieving products with orders with pagination", e);
            throw new RuntimeException("Failed to retrieve products with orders", e);
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
    public PagedResponse<ProductDTO> getProductsWithoutOrders(int page, int size, String sortBy, String sortOrder) {
        log.debug(
                "Retrieving products without orders with pagination - page: {}, size: {}, sortBy: {}, sortOrder: {}",
                page,
                size,
                sortBy,
                sortOrder);
        try {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Product> productPage = productRepository.findProductsWithoutOrders(pageable);
            log.debug(
                    "Found {} products without orders on page {} of {}",
                    productPage.getContent().size(),
                    page + 1,
                    productPage.getTotalPages());

            return PagedResponse.of(
                    productPage.map(product -> productMapper.productToProductDTO(product)), sortBy, sortOrder);
        } catch (Exception e) {
            log.error("Error retrieving products without orders with pagination", e);
            throw new RuntimeException("Failed to retrieve products without orders", e);
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
