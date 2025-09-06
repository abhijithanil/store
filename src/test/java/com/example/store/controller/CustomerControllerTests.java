package com.example.store.controller;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@ComponentScan(basePackageClasses = CustomerMapper.class)
class CustomerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setName("John Doe");
        customer.setId(1L);

        customerDTO = new CustomerDTO();
        customerDTO.setName("John Doe");
        customerDTO.setId(1L);
    }

    @Test
    void testCreateCustomer() throws Exception {
        when(customerService.createCustomer(customer)).thenReturn(customerDTO);

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testGetAllCustomers() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of(customerDTO));

        mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void testSearchCustomers() throws Exception {
        when(customerService.searchCustomersByName("john")).thenReturn(List.of(customerDTO));

        mockMvc.perform(get("/customer/search").param("q", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void testSearchCustomersWithEmptyQuery() throws Exception {
        when(customerService.searchCustomersByName("")).thenReturn(List.of(customerDTO));

        mockMvc.perform(get("/customer/search").param("q", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void testSearchCustomersWithNoResults() throws Exception {
        when(customerService.searchCustomersByName("nonexistent")).thenReturn(List.of());

        mockMvc.perform(get("/customer/search").param("q", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetCustomerById() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(customerDTO);

        mockMvc.perform(get("/customer/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testUpdateCustomer() throws Exception {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("Jane Smith");
        CustomerDTO updatedCustomerDTO = new CustomerDTO();
        updatedCustomerDTO.setId(1L);
        updatedCustomerDTO.setName("Jane Smith");

        when(customerService.updateCustomer(1L, updatedCustomer)).thenReturn(updatedCustomerDTO);

        mockMvc.perform(put("/customer/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Smith"));
    }

    @Test
    void testDeleteCustomer() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/customer/{id}", 1L)).andExpect(status().isNoContent());
    }
}
