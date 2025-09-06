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

/** The interface Order mapper. */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Order to order dto order dto.
     *
     * @param order the order
     * @return the order dto
     */
    @Mapping(
            target = "products",
            expression =
                    "java(order.getProducts() != null ? productsToProductDTOs(order.getProducts()) : java.util.Collections.emptyList())")
    OrderDTO orderToOrderDTO(Order order);

    /**
     * Orders to order dt os list.
     *
     * @param orders the orders
     * @return the list
     */
    List<OrderDTO> ordersToOrderDTOs(List<Order> orders);

    /**
     * Order to order customer dto order customer dto.
     *
     * @param customer the customer
     * @return the order customer dto
     */
    OrderCustomerDTO orderToOrderCustomerDTO(Customer customer);

    /**
     * Maps a list of Product entities to a list of ProductDTOs.
     *
     * @param products the list of product entities
     * @return the list of product DTOs
     */
    List<ProductDTO> productsToProductDTOs(List<Product> products);
}
