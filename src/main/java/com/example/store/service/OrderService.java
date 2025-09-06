package com.example.store.service;

import com.example.store.dto.CreateOrderRequest;
import com.example.store.dto.OrderDTO;
import com.example.store.dto.PagedResponse;

import java.util.List;
import java.util.Optional;

/**
 * The interface Order service.
 */
public interface OrderService {
    /**
     * Retrieves all orders with pagination support.
     *
     * @param page      the page number (0-based)
     * @param size      the page size
     * @param sortBy    the field to sort by
     * @param sortOrder the sort direction (asc/desc)
     * @return paged response of orders
     */
    PagedResponse<OrderDTO> getAllOrders(int page, int size, String sortBy, String sortOrder);

    /**
     * Retrieves all orders (legacy method for backward compatibility).
     *
     * @return list of all orders
     */
    List<OrderDTO> getAllOrders();

    /**
     * Gets order by id.
     *
     * @param id the id
     * @return the order by id
     */
    Optional<OrderDTO> getOrderById(Long id);

    /**
     * Creates a new order from the request data.
     *
     * @param request the order creation request
     * @return the created order DTO
     */
    OrderDTO createOrder(CreateOrderRequest request);
}
