package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;

import java.util.List;

/**
 * Service interface for customer operations following SOLID principles. Interface Segregation: Defines only
 * customer-related operations. Dependency Inversion: Depends on abstractions, not concrete implementations.
 */
public interface CustomerService {

    /**
     * Retrieves all customers.
     *
     * @return list of all customers
     */
    List<CustomerDTO> getAllCustomers();

    /**
     * Creates a new customer.
     *
     * @param customer the customer to create
     * @return the created customer DTO
     */
    CustomerDTO createCustomer(Customer customer);

    /**
     * Searches for customers whose name contains the given query string. The search is case-insensitive and matches any
     * substring within customer names.
     *
     * @param query the search query string
     * @return list of customers matching the search criteria
     */
    List<CustomerDTO> searchCustomersByName(String query);

    /**
     * Retrieves a customer by ID.
     *
     * @param id the customer ID
     * @return the customer DTO
     */
    CustomerDTO getCustomerById(Long id);

    /**
     * Updates an existing customer.
     *
     * @param id the customer ID
     * @param customer the updated customer data
     * @return the updated customer DTO
     */
    CustomerDTO updateCustomer(Long id, Customer customer);

    /**
     * Deletes a customer by ID.
     *
     * @param id the customer ID
     */
    void deleteCustomer(Long id);
}
