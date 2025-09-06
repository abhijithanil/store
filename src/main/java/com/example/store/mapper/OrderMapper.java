package com.example.store.mapper;

import com.example.store.dto.OrderCustomerDTO;
import com.example.store.dto.OrderDTO;
import com.example.store.dto.ProductDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for Order entity and DTO following SOLID principles. Single Responsibility: Handles mapping between
 * Order entity and OrderDTO.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(
            target = "products",
            expression =
                    "java(order.getProducts() != null ? productsToProductDTOs(order.getProducts()) : java.util.Collections.emptyList())")
    OrderDTO orderToOrderDTO(Order order);

    List<OrderDTO> ordersToOrderDTOs(List<Order> orders);

    OrderCustomerDTO orderToOrderCustomerDTO(Customer customer);

    /**
     * Maps a list of Product entities to a list of ProductDTOs.
     *
     * @param products the list of product entities
     * @return the list of product DTOs
     */
    List<ProductDTO> productsToProductDTOs(List<Product> products);
}
