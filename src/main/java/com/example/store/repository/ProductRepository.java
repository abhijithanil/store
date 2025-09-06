package com.example.store.repository;

import com.example.store.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for Product operations following SOLID principles. Interface Segregation: Defines only
 * product-related operations. Dependency Inversion: Depends on abstractions, not concrete implementations.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds products by description containing the given substring (case-insensitive). The search matches any word in
     * the product's description that contains the query string.
     *
     * @param query the substring to search for in product descriptions
     * @return list of products matching the search criteria
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> findByDescriptionContainingIgnoreCase(@Param("query") String query);

    /**
     * Finds products that are contained in orders.
     *
     * @return list of products that have associated orders
     */
    @Query("SELECT DISTINCT p FROM Product p JOIN p.orders o")
    List<Product> findProductsWithOrders();

    /**
     * Finds products that are not contained in any orders.
     *
     * @return list of products that have no associated orders
     */
    @Query("SELECT p FROM Product p LEFT JOIN p.orders o WHERE o IS NULL")
    List<Product> findProductsWithoutOrders();
}
