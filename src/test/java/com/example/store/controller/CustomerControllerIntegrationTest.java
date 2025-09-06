package com.example.store.controller;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CustomerController using mocked services. Tests complete request-response cycle.
 */
@WebMvcTest(CustomerController.class)
@DisplayName("CustomerController Integration Tests")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        reset(customerService);
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() throws Exception {
        // Given
        Customer customer = new Customer();
        customer.setName("John Doe");
        
        CustomerDTO savedCustomer = new CustomerDTO();
        savedCustomer.setId(1L);
        savedCustomer.setName("John Doe");
        
        when(customerService.createCustomer(any(Customer.class))).thenReturn(savedCustomer);

        // When & Then
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(customerService).createCustomer(any(Customer.class));
    }

    @Test
    @DisplayName("Should return validation error for empty customer name")
    void shouldReturnValidationErrorForEmptyCustomerName() throws Exception {
        // Given
        Customer customer = new Customer();
        customer.setName("");

        // When & Then
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.name").exists());

        verify(customerService, never()).createCustomer(any(Customer.class));
    }

    @Test
    @DisplayName("Should return validation error for null customer name")
    void shouldReturnValidationErrorForNullCustomerName() throws Exception {
        // Given
        Customer customer = new Customer();
        customer.setName(null);

        // When & Then
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.name").value("Customer name is required"));

        verify(customerService, never()).createCustomer(any(Customer.class));
    }

    @Test
    @DisplayName("Should return validation error for customer name too long")
    void shouldReturnValidationErrorForCustomerNameTooLong() throws Exception {
        // Given
        Customer customer = new Customer();
        customer.setName("a".repeat(256)); // Exceeds 255 character limit

        // When & Then
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.name")
                        .value("Customer name must be between 1 and 255 characters"));

        verify(customerService, never()).createCustomer(any(Customer.class));
    }

    @Test
    @DisplayName("Should get all customers")
    void shouldGetAllCustomers() throws Exception {
        // Given
        CustomerDTO customer1 = new CustomerDTO();
        customer1.setId(1L);
        customer1.setName("John Doe");
        
        CustomerDTO customer2 = new CustomerDTO();
        customer2.setId(2L);
        customer2.setName("Jane Smith");
        
        List<CustomerDTO> customers = Arrays.asList(customer1, customer2);
        when(customerService.getAllCustomers()).thenReturn(customers);

        // When & Then
        mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("John Doe", "Jane Smith")));

        verify(customerService).getAllCustomers();
    }

    @Test
    @DisplayName("Should get customer by ID")
    void shouldGetCustomerById() throws Exception {
        // Given
        CustomerDTO customer = new CustomerDTO();
        customer.setId(1L);
        customer.setName("John Doe");
        
        when(customerService.getCustomerById(1L)).thenReturn(customer);

        // When & Then
        mockMvc.perform(get("/customer/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(customerService).getCustomerById(1L);
    }

    @Test
    @DisplayName("Should return 404 when customer not found by ID")
    void shouldReturn404WhenCustomerNotFoundById() throws Exception {
        // Given
        when(customerService.getCustomerById(999L))
                .thenThrow(new com.example.store.exception.CustomerNotFoundException("Customer not found with ID: 999"));

        // When & Then
        mockMvc.perform(get("/customer/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Customer Not Found"))
                .andExpect(jsonPath("$.message").value("Customer not found with ID: 999"));

        verify(customerService).getCustomerById(999L);
    }

    @Test
    @DisplayName("Should return 400 for invalid customer ID")
    void shouldReturn400ForInvalidCustomerId() throws Exception {
        // Given
        when(customerService.getCustomerById(-1L))
                .thenThrow(new com.example.store.exception.ValidationException("Customer ID must be positive"));

        // When & Then
        mockMvc.perform(get("/customer/{id}", -1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Customer ID must be positive"));

        verify(customerService).getCustomerById(-1L);
    }

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() throws Exception {
        // Given
        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("John Updated");
        
        CustomerDTO savedCustomer = new CustomerDTO();
        savedCustomer.setId(1L);
        savedCustomer.setName("John Updated");
        
        when(customerService.updateCustomer(eq(1L), any(Customer.class))).thenReturn(savedCustomer);

        // When & Then
        mockMvc.perform(put("/customer/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Updated"));

        verify(customerService).updateCustomer(eq(1L), any(Customer.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent customer")
    void shouldReturn404WhenUpdatingNonExistentCustomer() throws Exception {
        // Given
        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("John Updated");
        
        when(customerService.updateCustomer(eq(999L), any(Customer.class)))
                .thenThrow(new com.example.store.exception.CustomerNotFoundException("Customer not found with ID: 999"));

        // When & Then
        mockMvc.perform(put("/customer/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Customer Not Found"))
                .andExpect(jsonPath("$.message").value("Customer not found with ID: 999"));

        verify(customerService).updateCustomer(eq(999L), any(Customer.class));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() throws Exception {
        // Given
        doNothing().when(customerService).deleteCustomer(1L);

        // When & Then
        mockMvc.perform(delete("/customer/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomer(1L);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent customer")
    void shouldReturn404WhenDeletingNonExistentCustomer() throws Exception {
        // Given
        doThrow(new com.example.store.exception.CustomerNotFoundException("Customer not found with ID: 999"))
                .when(customerService).deleteCustomer(999L);

        // When & Then
        mockMvc.perform(delete("/customer/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Customer Not Found"))
                .andExpect(jsonPath("$.message").value("Customer not found with ID: 999"));

        verify(customerService).deleteCustomer(999L);
    }

    @Test
    @DisplayName("Should search customers by name")
    void shouldSearchCustomersByName() throws Exception {
        // Given
        CustomerDTO customer1 = new CustomerDTO();
        customer1.setId(1L);
        customer1.setName("John Doe");
        
        CustomerDTO customer2 = new CustomerDTO();
        customer2.setId(2L);
        customer2.setName("Bob Johnson");
        
        List<CustomerDTO> customers = Arrays.asList(customer1, customer2);
        when(customerService.searchCustomersByName("john")).thenReturn(customers);

        // When & Then
        mockMvc.perform(get("/customer/search").param("q", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("John Doe", "Bob Johnson")));

        verify(customerService).searchCustomersByName("john");
    }

    @Test
    @DisplayName("Should return all customers when search query is empty")
    void shouldReturnAllCustomersWhenSearchQueryIsEmpty() throws Exception {
        // Given
        CustomerDTO customer1 = new CustomerDTO();
        customer1.setId(1L);
        customer1.setName("John Doe");
        
        CustomerDTO customer2 = new CustomerDTO();
        customer2.setId(2L);
        customer2.setName("Jane Smith");
        
        List<CustomerDTO> customers = Arrays.asList(customer1, customer2);
        when(customerService.searchCustomersByName("")).thenReturn(customers);

        // When & Then
        mockMvc.perform(get("/customer/search").param("q", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("John Doe", "Jane Smith")));

        verify(customerService).searchCustomersByName("");
    }

    @Test
    @DisplayName("Should return empty list when no customers match search")
    void shouldReturnEmptyListWhenNoCustomersMatchSearch() throws Exception {
        // Given
        when(customerService.searchCustomersByName("nonexistent")).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/customer/search").param("q", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(customerService).searchCustomersByName("nonexistent");
    }

    @Test
    @DisplayName("Should return 400 for invalid search query")
    void shouldReturn400ForInvalidSearchQuery() throws Exception {
        // Given
        when(customerService.searchCustomersByName("john123"))
                .thenThrow(new com.example.store.exception.ValidationException("Search query contains invalid characters"));

        // When & Then
        mockMvc.perform(get("/customer/search").param("q", "john123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Search query contains invalid characters"));

        verify(customerService).searchCustomersByName("john123");
    }

    @Test
    @DisplayName("Should handle case-insensitive search")
    void shouldHandleCaseInsensitiveSearch() throws Exception {
        // Given
        CustomerDTO customer = new CustomerDTO();
        customer.setId(1L);
        customer.setName("John Doe");
        
        when(customerService.searchCustomersByName("JOHN")).thenReturn(Arrays.asList(customer));
        when(customerService.searchCustomersByName("john")).thenReturn(Arrays.asList(customer));
        when(customerService.searchCustomersByName("JoHn")).thenReturn(Arrays.asList(customer));

        // When & Then
        mockMvc.perform(get("/customer/search").param("q", "JOHN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        mockMvc.perform(get("/customer/search").param("q", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        mockMvc.perform(get("/customer/search").param("q", "JoHn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        verify(customerService).searchCustomersByName("JOHN");
        verify(customerService).searchCustomersByName("john");
        verify(customerService).searchCustomersByName("JoHn");
    }

    @Test
    @DisplayName("Should sanitize customer names on creation")
    void shouldSanitizeCustomerNamesOnCreation() throws Exception {
        // Given
        Customer customer = new Customer();
        customer.setName("  john doe  "); // Extra whitespace
        
        CustomerDTO savedCustomer = new CustomerDTO();
        savedCustomer.setId(1L);
        savedCustomer.setName("John Doe"); // Sanitized
        
        when(customerService.createCustomer(any(Customer.class))).thenReturn(savedCustomer);

        // When & Then
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe")); // Should be sanitized

        verify(customerService).createCustomer(any(Customer.class));
    }
}
