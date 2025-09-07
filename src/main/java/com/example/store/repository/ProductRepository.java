package com.example.store.repository;

import com.example.store.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/** The interface Product repository. */
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
     * Finds products by description containing the given substring (case-insensitive) with pagination support.
     *
     * @param query the substring to search for in product descriptions
     * @param pageable pagination information
     * @return page of products matching the search criteria
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Product> findByDescriptionContainingIgnoreCase(@Param("query") String query, Pageable pageable);

    /**
     * Finds products that are contained in orders.
     *
     * @return list of products that have associated orders
     */
    @Query("SELECT DISTINCT p FROM Product p JOIN p.orders o")
    List<Product> findProductsWithOrders();

    /**
     * Finds products that are contained in orders with pagination support.
     *
     * @param pageable pagination information
     * @return page of products that have associated orders
     */
    @Query("SELECT DISTINCT p FROM Product p JOIN p.orders o")
    Page<Product> findProductsWithOrders(Pageable pageable);

    /**
     * Finds products that are not contained in any orders.
     *
     * @return list of products that have no associated orders
     */
    @Query("SELECT p FROM Product p LEFT JOIN p.orders o WHERE o IS NULL")
    List<Product> findProductsWithoutOrders();

    /**
     * Finds products that are not contained in any orders with pagination support.
     *
     * @param pageable pagination information
     * @return page of products that have no associated orders
     */
    @Query("SELECT p FROM Product p LEFT JOIN p.orders o WHERE o IS NULL")
    Page<Product> findProductsWithoutOrders(Pageable pageable);
}
