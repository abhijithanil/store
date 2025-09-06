package com.example.store.service;

import com.example.store.dto.OrderDTO;
import com.example.store.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<OrderDTO> getAllOrders();

    Optional<OrderDTO> getOrderById(Long id);

    OrderDTO createOrder(Order order);
}
