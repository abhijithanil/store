package com.example.store.service.impl;

import com.example.store.dto.CustomerDTO;
import com.example.store.dto.PagedResponse;
import com.example.store.entity.Customer;
import com.example.store.exception.CustomerNotFoundException;
import com.example.store.exception.ValidationException;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.service.CustomerService;
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

import java.util.Optional;

/** The type Customer service. */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final ValidationService validationService;

    @Override
    @Cacheable(value = "pagedCustomers", key = "#page + '_' + #size + '_' + #sortBy + '_' + #sortOrder")
    public PagedResponse<CustomerDTO> getAllCustomers(int page, int size, String sortBy, String sortOrder) {
        log.debug(
                "Retrieving customers with pagination - page: {}, size: {}, sortBy: {}, sortOrder: {}",
                page,
                size,
                sortBy,
                sortOrder);
        try {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Customer> customerPage = customerRepository.findAll(pageable);

            log.debug(
                    "Found {} customers on page {} of {}",
                    customerPage.getContent().size(),
                    page + 1,
                    customerPage.getTotalPages());
            return PagedResponse.of(
                    customerPage.map(customer -> customerMapper.customerToCustomerDTO(customer)), sortBy, sortOrder);
        } catch (Exception e) {
            log.error("Error retrieving customers with pagination", e);
            throw new RuntimeException("Failed to retrieve customers", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(
            value = {"customers", "pagedCustomers"},
            allEntries = true)
    public CustomerDTO createCustomer(Customer customer) {
        log.debug("Creating new customer: {}", customer);

        // Validate input
        validationService.validateCustomerName(customer.getName());

        try {
            // Sanitize input
            customer.setName(validationService.sanitizeName(customer.getName()));

            Customer savedCustomer = customerRepository.save(customer);
            log.info("Successfully created customer with ID: {}", savedCustomer.getId());

            return customerMapper.customerToCustomerDTO(savedCustomer);
        } catch (Exception e) {
            log.error("Error creating customer: {}", customer, e);
            throw new RuntimeException("Failed to create customer", e);
        }
    }

    @Override
    public PagedResponse<CustomerDTO> searchCustomersByName(
            String query, int page, int size, String sortBy, String sortOrder) {
        log.debug(
                "Searching customers with pagination - query: {}, page: {}, size: {}, sortBy: {}, sortOrder: {}",
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

            Page<Customer> customerPage;
            if (query == null || query.trim().isEmpty()) {
                log.debug("Empty query, returning all customers with pagination");
                customerPage = customerRepository.findAll(pageable);
            } else {
                String sanitizedQuery = query.trim();
                customerPage = customerRepository.findByNameContainingIgnoreCase(sanitizedQuery, pageable);
                log.debug(
                        "Found {} customers matching query: {} on page {} of {}",
                        customerPage.getContent().size(),
                        sanitizedQuery,
                        page + 1,
                        customerPage.getTotalPages());
            }

            return PagedResponse.of(
                    customerPage.map(customer -> customerMapper.customerToCustomerDTO(customer)), sortBy, sortOrder);
        } catch (ValidationException e) {
            log.warn("Validation error in search: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error searching customers with pagination - query: {}", query, e);
            throw new RuntimeException("Failed to search customers", e);
        }
    }

    @Override
    @Cacheable(value = "customers", key = "#id")
    public CustomerDTO getCustomerById(Long id) {
        log.debug("Retrieving customer with ID: {}", id);

        validationService.validateCustomerId(id);

        try {
            Optional<Customer> customer = customerRepository.findById(id);
            if (customer.isEmpty()) {
                log.warn("Customer not found with ID: {}", id);
                throw CustomerNotFoundException.withId(id);
            }

            log.debug("Successfully retrieved customer with ID: {}", id);
            return customerMapper.customerToCustomerDTO(customer.get());
        } catch (CustomerNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving customer with ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve customer", e);
        }
    }
}
