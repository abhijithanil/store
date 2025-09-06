package com.example.store.mapper;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;

import org.mapstruct.Mapper;

import java.util.List;

/** The interface Customer mapper. */
@Mapper(componentModel = "spring")
public interface CustomerMapper {
    /**
     * Customer to customer dto customer dto.
     *
     * @param customer the customer
     * @return the customer dto
     */
    CustomerDTO customerToCustomerDTO(Customer customer);

    /**
     * Customers to customer dt os list.
     *
     * @param customer the customer
     * @return the list
     */
    List<CustomerDTO> customersToCustomerDTOs(List<Customer> customer);
}
