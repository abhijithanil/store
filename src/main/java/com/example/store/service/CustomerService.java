package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.dto.PagedResponse;
import com.example.store.entity.Customer;

/** The interface Customer service. */
public interface CustomerService {

    /**
     * Retrieves all customers with pagination support.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the field to sort by
     * @param sortOrder the sort direction (asc/desc)
     * @return paged response of customers
     */
    PagedResponse<CustomerDTO> getAllCustomers(int page, int size, String sortBy, String sortOrder);

    /**
     * Creates a new customer.
     *
     * @param customer the customer to create
     * @return the created customer DTO
     */
    CustomerDTO createCustomer(Customer customer);

    /**
     * Searches for customers whose name contains the given query string with pagination support.
     *
     * @param query the search query string
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the field to sort by
     * @param sortOrder the sort direction (asc/desc)
     * @return paged response of customers matching the search criteria
     */
    PagedResponse<CustomerDTO> searchCustomersByName(String query, int page, int size, String sortBy, String sortOrder);

    /**
     * Retrieves a customer by ID.
     *
     * @param id the customer ID
     * @return the customer DTO
     */
    CustomerDTO getCustomerById(Long id);
}
