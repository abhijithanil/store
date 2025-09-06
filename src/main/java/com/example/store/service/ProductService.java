package com.example.store.service;

import com.example.store.dto.PagedResponse;
import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;

import java.util.List;

/**
 * Service interface for product operations following SOLID principles. Interface Segregation: Defines only
 * product-related operations. Dependency Inversion: Depends on abstractions, not concrete implementations.
 */
public interface ProductService {

    /**
     * Retrieves all products with pagination support.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the field to sort by
     * @param sortOrder the sort direction (asc/desc)
     * @return paged response of products with their associated order IDs
     */
    PagedResponse<ProductDTO> getAllProducts(int page, int size, String sortBy, String sortOrder);

    /**
     * Retrieves all products (legacy method for backward compatibility).
     *
     * @return list of all products with their associated order IDs
     */
    List<ProductDTO> getAllProducts();

    /**
     * Creates a new product.
     *
     * @param product the product to create
     * @return the created product DTO
     */
    ProductDTO createProduct(Product product);

    /**
     * Retrieves a product by ID.
     *
     * @param id the product ID
     * @return the product DTO
     */
    ProductDTO getProductById(Long id);

    /**
     * Updates an existing product.
     *
     * @param id the product ID
     * @param product the updated product data
     * @return the updated product DTO
     */
    ProductDTO updateProduct(Long id, Product product);

    /**
     * Deletes a product by ID.
     *
     * @param id the product ID
     */
    void deleteProduct(Long id);

    /**
     * Searches for products whose description contains the given query string with pagination support.
     *
     * @param query the search query string
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the field to sort by
     * @param sortOrder the sort direction (asc/desc)
     * @return paged response of products matching the search criteria
     */
    PagedResponse<ProductDTO> searchProductsByDescription(
            String query, int page, int size, String sortBy, String sortOrder);

    /**
     * Searches for products whose description contains the given query string (legacy method for backward
     * compatibility).
     *
     * @param query the search query string
     * @return list of products matching the search criteria
     */
    List<ProductDTO> searchProductsByDescription(String query);

    /**
     * Retrieves products that are contained in orders with pagination support.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the field to sort by
     * @param sortOrder the sort direction (asc/desc)
     * @return paged response of products that have associated orders
     */
    PagedResponse<ProductDTO> getProductsWithOrders(int page, int size, String sortBy, String sortOrder);

    /**
     * Retrieves products that are contained in orders (legacy method for backward compatibility).
     *
     * @return list of products that have associated orders
     */
    List<ProductDTO> getProductsWithOrders();

    /**
     * Retrieves products that are not contained in any orders with pagination support.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the field to sort by
     * @param sortOrder the sort direction (asc/desc)
     * @return paged response of products that have no associated orders
     */
    PagedResponse<ProductDTO> getProductsWithoutOrders(int page, int size, String sortBy, String sortOrder);

    /**
     * Retrieves products that are not contained in any orders (legacy method for backward compatibility).
     *
     * @return list of products that have no associated orders
     */
    List<ProductDTO> getProductsWithoutOrders();
}
