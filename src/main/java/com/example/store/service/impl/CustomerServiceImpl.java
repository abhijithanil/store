package com.example.store.service.impl;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.exception.CustomerNotFoundException;
import com.example.store.exception.ValidationException;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.service.CustomerService;
import com.example.store.service.ValidationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of CustomerService following SOLID principles. Single Responsibility: Manages customer business logic.
 * Open/Closed: Extensible through interface. Dependency Inversion: Depends on abstractions (ValidationService,
 * CustomerRepository).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final ValidationService validationService;

    @Override
    public List<CustomerDTO> getAllCustomers() {
        log.debug("Retrieving all customers");
        try {
            List<Customer> customers = customerRepository.findAll();
            log.debug("Found {} customers", customers.size());
            return customerMapper.customersToCustomerDTOs(customers);
        } catch (Exception e) {
            log.error("Error retrieving all customers", e);
            throw new RuntimeException("Failed to retrieve customers", e);
        }
    }

    @Override
    @Transactional
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
    public List<CustomerDTO> searchCustomersByName(String query) {
        log.debug("Searching customers with query: {}", query);

        try {
            // Validate search query
            validationService.validateSearchQuery(query);

            List<Customer> customers;
            if (query == null || query.trim().isEmpty()) {
                log.debug("Empty query, returning all customers");
                customers = customerRepository.findAll();
            } else {
                String sanitizedQuery = query.trim();
                customers = customerRepository.findByNameContainingIgnoreCase(sanitizedQuery);
                log.debug("Found {} customers matching query: {}", customers.size(), sanitizedQuery);
            }

            return customerMapper.customersToCustomerDTOs(customers);
        } catch (ValidationException e) {
            log.warn("Validation error in search: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error searching customers with query: {}", query, e);
            throw new RuntimeException("Failed to search customers", e);
        }
    }

    @Override
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

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, Customer customer) {
        log.debug("Updating customer with ID: {}", id);

        validationService.validateCustomerId(id);
        validationService.validateCustomerName(customer.getName());

        try {
            Optional<Customer> existingCustomer = customerRepository.findById(id);
            if (existingCustomer.isEmpty()) {
                log.warn("Customer not found for update with ID: {}", id);
                throw CustomerNotFoundException.withId(id);
            }

            Customer customerToUpdate = existingCustomer.get();
            customerToUpdate.setName(validationService.sanitizeName(customer.getName()));

            Customer updatedCustomer = customerRepository.save(customerToUpdate);
            log.info("Successfully updated customer with ID: {}", id);

            return customerMapper.customerToCustomerDTO(updatedCustomer);
        } catch (CustomerNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating customer with ID: {}", id, e);
            throw new RuntimeException("Failed to update customer", e);
        }
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        log.debug("Deleting customer with ID: {}", id);

        validationService.validateCustomerId(id);

        try {
            if (!customerRepository.existsById(id)) {
                log.warn("Customer not found for deletion with ID: {}", id);
                throw CustomerNotFoundException.withId(id);
            }

            customerRepository.deleteById(id);
            log.info("Successfully deleted customer with ID: {}", id);
        } catch (CustomerNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting customer with ID: {}", id, e);
            throw new RuntimeException("Failed to delete customer", e);
        }
    }
}
