package com.example.store.repository;

import com.example.store.entity.Customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * The interface Customer repository.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find by name containing ignore case list.
     *
     * @param query the query
     * @return the list
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Customer> findByNameContainingIgnoreCase(@Param("query") String query);

    /**
     * Find by name containing ignore case page.
     *
     * @param query    the query
     * @param pageable the pageable
     * @return the page
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Customer> findByNameContainingIgnoreCase(@Param("query") String query, Pageable pageable);
}
