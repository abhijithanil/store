package com.example.store.service.impl;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.exception.CustomerNotFoundException;
import com.example.store.exception.ValidationException;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.service.ValidationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** The type Customer service impl test. */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceImpl Tests")
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    /** Sets up. */
    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("John Doe");
    }

    /** Should create customer successfully. */
    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() {
        // Given
        Customer customerToCreate = new Customer();
        customerToCreate.setName("Jane Smith");
        Customer savedCustomer = new Customer();
        savedCustomer.setId(2L);
        savedCustomer.setName("Jane Smith");
        CustomerDTO savedCustomerDTO = new CustomerDTO();
        savedCustomerDTO.setId(2L);
        savedCustomerDTO.setName("Jane Smith");

        doNothing().when(validationService).validateCustomerName("Jane Smith");
        when(validationService.sanitizeName("Jane Smith")).thenReturn("Jane Smith");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(customerMapper.customerToCustomerDTO(savedCustomer)).thenReturn(savedCustomerDTO);

        // When
        CustomerDTO result = customerService.createCustomer(customerToCreate);

        // Then
        assertNotNull(result);
        assertEquals(savedCustomerDTO, result);
        verify(validationService).validateCustomerName("Jane Smith");
        verify(validationService).sanitizeName("Jane Smith");
        verify(customerRepository).save(customerToCreate);
        verify(customerMapper).customerToCustomerDTO(savedCustomer);
    }

    /** Should throw validation exception when creating customer with invalid name. */
    @Test
    @DisplayName("Should throw ValidationException when creating customer with invalid name")
    void shouldThrowValidationExceptionWhenCreatingCustomerWithInvalidName() {
        // Given
        Customer customerToCreate = new Customer();
        customerToCreate.setName("");

        doThrow(new ValidationException("Required field 'name' is missing or empty"))
                .when(validationService)
                .validateCustomerName("");

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> customerService.createCustomer(customerToCreate));
        assertEquals("Required field 'name' is missing or empty", exception.getMessage());
        verify(validationService).validateCustomerName("");
        verify(customerRepository, never()).save(any());
    }

    /** Should get customer by id successfully. */
    @Test
    @DisplayName("Should get customer by ID successfully")
    void shouldGetCustomerByIdSuccessfully() {
        // Given
        Long customerId = 1L;
        doNothing().when(validationService).validateCustomerId(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.customerToCustomerDTO(customer)).thenReturn(customerDTO);

        // When
        CustomerDTO result = customerService.getCustomerById(customerId);

        // Then
        assertNotNull(result);
        assertEquals(customerDTO, result);
        verify(validationService).validateCustomerId(customerId);
        verify(customerRepository).findById(customerId);
        verify(customerMapper).customerToCustomerDTO(customer);
    }

    /** Should throw customer not found exception when customer not found by id. */
    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer not found by ID")
    void shouldThrowCustomerNotFoundExceptionWhenCustomerNotFoundById() {
        // Given
        Long customerId = 999L;
        doNothing().when(validationService).validateCustomerId(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        CustomerNotFoundException exception =
                assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(customerId));
        assertEquals("Customer not found with ID: 999", exception.getMessage());
        verify(validationService).validateCustomerId(customerId);
        verify(customerRepository).findById(customerId);
        verify(customerMapper, never()).customerToCustomerDTO(any());
    }

    /** Should search customers by name successfully. */
    @Test
    @DisplayName("Should search customers by name successfully")
    void shouldSearchCustomersByNameSuccessfully() {
        // Given
        String query = "john";
        List<Customer> customers = Arrays.asList(customer);
        Page<Customer> customerPage = new PageImpl<>(customers);
        
        doNothing().when(validationService).validateSearchQuery(query);
        when(customerRepository.findByNameContainingIgnoreCase(eq(query), any(Pageable.class))).thenReturn(customerPage);

        // When
        customerService.searchCustomersByName(query, 0, 10, "name", "asc");

        // Then
        verify(validationService).validateSearchQuery(query);
        verify(customerRepository).findByNameContainingIgnoreCase(eq(query), any(Pageable.class));
    }

    /** Should return all customers when search query is empty. */
    @Test
    @DisplayName("Should return all customers when search query is empty")
    void shouldReturnAllCustomersWhenSearchQueryIsEmpty() {
        // Given
        String query = "";
        List<Customer> customers = Arrays.asList(customer);
        Page<Customer> customerPage = new PageImpl<>(customers);
        
        doNothing().when(validationService).validateSearchQuery(query);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(customerPage);

        // When
        customerService.searchCustomersByName(query, 0, 10, "name", "asc");

        // Then
        verify(validationService).validateSearchQuery(query);
        verify(customerRepository).findAll(any(Pageable.class));
    }

    /** Should throw validation exception when search query is invalid. */
    @Test
    @DisplayName("Should throw ValidationException when search query is invalid")
    void shouldThrowValidationExceptionWhenSearchQueryIsInvalid() {
        // Given
        String invalidQuery = "a"; // Too short

        doThrow(new ValidationException("Invalid search query"))
                .when(validationService)
                .validateSearchQuery(invalidQuery);

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class, () -> customerService.searchCustomersByName(invalidQuery, 0, 10, "name", "asc"));
        assertEquals("Invalid search query", exception.getMessage());
        verify(validationService).validateSearchQuery(invalidQuery);
        verify(customerRepository, never()).findByNameContainingIgnoreCase(any(), any());
    }

    /** Should handle repository exception in search gracefully. */
    @Test
    @DisplayName("Should handle repository exception in search gracefully")
    void shouldHandleRepositoryExceptionInSearchGracefully() {
        // Given
        String query = "john";
        doNothing().when(validationService).validateSearchQuery(query);
        when(customerRepository.findByNameContainingIgnoreCase(eq(query), any(org.springframework.data.domain.Pageable.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> customerService.searchCustomersByName(query, 0, 10, "name", "asc"));
        assertEquals("Failed to search customers", exception.getMessage());
        verify(validationService).validateSearchQuery(query);
        verify(customerRepository).findByNameContainingIgnoreCase(eq(query), any(org.springframework.data.domain.Pageable.class));
    }

    /** Should handle repository exception in create gracefully. */
    @Test
    @DisplayName("Should handle repository exception in create gracefully")
    void shouldHandleRepositoryExceptionInCreateGracefully() {
        // Given
        Customer customerToCreate = new Customer();
        customerToCreate.setName("Jane Smith");

        doNothing().when(validationService).validateCustomerName("Jane Smith");
        when(validationService.sanitizeName("Jane Smith")).thenReturn("Jane Smith");
        when(customerRepository.save(any(Customer.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> customerService.createCustomer(customerToCreate));
        assertEquals("Failed to create customer", exception.getMessage());
        verify(validationService).validateCustomerName("Jane Smith");
        verify(validationService).sanitizeName("Jane Smith");
        verify(customerRepository).save(customerToCreate);
    }

    /** Should handle repository exception in get by id gracefully. */
    @Test
    @DisplayName("Should handle repository exception in get by id gracefully")
    void shouldHandleRepositoryExceptionInGetByIdGracefully() {
        // Given
        Long customerId = 1L;
        doNothing().when(validationService).validateCustomerId(customerId);
        when(customerRepository.findById(customerId))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> customerService.getCustomerById(customerId));
        assertEquals("Failed to retrieve customer", exception.getMessage());
        verify(validationService).validateCustomerId(customerId);
        verify(customerRepository).findById(customerId);
    }
}
