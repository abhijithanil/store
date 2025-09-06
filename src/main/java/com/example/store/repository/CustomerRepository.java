package com.example.store.repository;

import com.example.store.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds customers whose name contains the given substring (case-insensitive). The search matches any word in the
     * customer's name that contains the query string.
     *
     * @param query the substring to search for in customer names
     * @return list of customers matching the search criteria
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Customer> findByNameContainingIgnoreCase(@Param("query") String query);
}
