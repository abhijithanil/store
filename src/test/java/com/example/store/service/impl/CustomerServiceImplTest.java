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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerServiceImpl following SOLID principles. Tests all business logic scenarios and corner cases.
 */
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

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("John Doe");
    }

    @Test
    @DisplayName("Should retrieve all customers successfully")
    void shouldRetrieveAllCustomersSuccessfully() {
        // Given
        List<Customer> customers = Arrays.asList(customer);
        List<CustomerDTO> customerDTOs = Arrays.asList(customerDTO);
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.customersToCustomerDTOs(customers)).thenReturn(customerDTOs);

        // When
        List<CustomerDTO> result = customerService.getAllCustomers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customerDTO, result.get(0));
        verify(customerRepository).findAll();
        verify(customerMapper).customersToCustomerDTOs(customers);
    }

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

    @Test
    @DisplayName("Should search customers with valid query")
    void shouldSearchCustomersWithValidQuery() {
        // Given
        String query = "john";
        List<Customer> customers = Arrays.asList(customer);
        List<CustomerDTO> customerDTOs = Arrays.asList(customerDTO);

        doNothing().when(validationService).validateSearchQuery(query);
        when(customerRepository.findByNameContainingIgnoreCase(query)).thenReturn(customers);
        when(customerMapper.customersToCustomerDTOs(customers)).thenReturn(customerDTOs);

        // When
        List<CustomerDTO> result = customerService.searchCustomersByName(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customerDTO, result.get(0));
        verify(validationService).validateSearchQuery(query);
        verify(customerRepository).findByNameContainingIgnoreCase(query);
        verify(customerMapper).customersToCustomerDTOs(customers);
    }

    @Test
    @DisplayName("Should return all customers when search query is null")
    void shouldReturnAllCustomersWhenSearchQueryIsNull() {
        // Given
        String query = null;
        List<Customer> customers = Arrays.asList(customer);
        List<CustomerDTO> customerDTOs = Arrays.asList(customerDTO);

        doNothing().when(validationService).validateSearchQuery(query);
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.customersToCustomerDTOs(customers)).thenReturn(customerDTOs);

        // When
        List<CustomerDTO> result = customerService.searchCustomersByName(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(validationService).validateSearchQuery(query);
        verify(customerRepository).findAll();
        verify(customerRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("Should return all customers when search query is empty")
    void shouldReturnAllCustomersWhenSearchQueryIsEmpty() {
        // Given
        String query = "";
        List<Customer> customers = Arrays.asList(customer);
        List<CustomerDTO> customerDTOs = Arrays.asList(customerDTO);

        doNothing().when(validationService).validateSearchQuery(query);
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.customersToCustomerDTOs(customers)).thenReturn(customerDTOs);

        // When
        List<CustomerDTO> result = customerService.searchCustomersByName(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(validationService).validateSearchQuery(query);
        verify(customerRepository).findAll();
        verify(customerRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("Should throw ValidationException when search query is invalid")
    void shouldThrowValidationExceptionWhenSearchQueryIsInvalid() {
        // Given
        String invalidQuery = "john123";

        doThrow(new ValidationException("Search query contains invalid characters"))
                .when(validationService)
                .validateSearchQuery(invalidQuery);

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> customerService.searchCustomersByName(invalidQuery));
        assertEquals("Search query contains invalid characters", exception.getMessage());
        verify(validationService).validateSearchQuery(invalidQuery);
        verify(customerRepository, never()).findAll();
        verify(customerRepository, never()).findByNameContainingIgnoreCase(any());
    }

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

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() {
        // Given
        Long customerId = 1L;
        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("Jane Smith");
        Customer savedCustomer = new Customer();
        savedCustomer.setId(customerId);
        savedCustomer.setName("Jane Smith");
        CustomerDTO savedCustomerDTO = new CustomerDTO();
        savedCustomerDTO.setId(customerId);
        savedCustomerDTO.setName("Jane Smith");

        doNothing().when(validationService).validateCustomerId(customerId);
        doNothing().when(validationService).validateCustomerName("Jane Smith");
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(validationService.sanitizeName("Jane Smith")).thenReturn("Jane Smith");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(customerMapper.customerToCustomerDTO(savedCustomer)).thenReturn(savedCustomerDTO);

        // When
        CustomerDTO result = customerService.updateCustomer(customerId, updatedCustomer);

        // Then
        assertNotNull(result);
        assertEquals(savedCustomerDTO, result);
        verify(validationService).validateCustomerId(customerId);
        verify(validationService).validateCustomerName("Jane Smith");
        verify(customerRepository).findById(customerId);
        verify(validationService).sanitizeName("Jane Smith");
        verify(customerRepository).save(any(Customer.class));
        verify(customerMapper).customerToCustomerDTO(savedCustomer);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when updating non-existent customer")
    void shouldThrowCustomerNotFoundExceptionWhenUpdatingNonExistentCustomer() {
        // Given
        Long customerId = 999L;
        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("Jane Smith");

        doNothing().when(validationService).validateCustomerId(customerId);
        doNothing().when(validationService).validateCustomerName("Jane Smith");
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class, () -> customerService.updateCustomer(customerId, updatedCustomer));
        assertEquals("Customer not found with ID: 999", exception.getMessage());
        verify(validationService).validateCustomerId(customerId);
        verify(validationService).validateCustomerName("Jane Smith");
        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() {
        // Given
        Long customerId = 1L;
        doNothing().when(validationService).validateCustomerId(customerId);
        when(customerRepository.existsById(customerId)).thenReturn(true);

        // When
        assertDoesNotThrow(() -> customerService.deleteCustomer(customerId));

        // Then
        verify(validationService).validateCustomerId(customerId);
        verify(customerRepository).existsById(customerId);
        verify(customerRepository).deleteById(customerId);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when deleting non-existent customer")
    void shouldThrowCustomerNotFoundExceptionWhenDeletingNonExistentCustomer() {
        // Given
        Long customerId = 999L;
        doNothing().when(validationService).validateCustomerId(customerId);
        when(customerRepository.existsById(customerId)).thenReturn(false);

        // When & Then
        CustomerNotFoundException exception =
                assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(customerId));
        assertEquals("Customer not found with ID: 999", exception.getMessage());
        verify(validationService).validateCustomerId(customerId);
        verify(customerRepository).existsById(customerId);
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should handle repository exception gracefully")
    void shouldHandleRepositoryExceptionGracefully() {
        // Given
        when(customerRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> customerService.getAllCustomers());
        assertEquals("Failed to retrieve customers", exception.getMessage());
        verify(customerRepository).findAll();
    }
}
