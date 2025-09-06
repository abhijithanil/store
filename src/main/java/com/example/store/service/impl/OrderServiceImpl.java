package com.example.store.service.impl;

import com.example.store.dto.CreateOrderRequest;
import com.example.store.dto.OrderDTO;
import com.example.store.dto.PagedResponse;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;
import com.example.store.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    @Cacheable(value = "pagedOrders", key = "#page + '_' + #size + '_' + #sortBy + '_' + #sortOrder")
    public PagedResponse<OrderDTO> getAllOrders(int page, int size, String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Order> orderPage = orderRepository.findAll(pageable);
        return PagedResponse.of(orderPage.map(order -> orderMapper.orderToOrderDTO(order)), sortBy, sortOrder);
    }

    @Override
    @Cacheable(value = "orders", key = "'all'")
    public List<OrderDTO> getAllOrders() {
        return orderMapper.ordersToOrderDTOs(orderRepository.findAll());
    }

    @Override
    @Cacheable(value = "orders", key = "#id")
    public Optional<OrderDTO> getOrderById(Long id) {
        return orderRepository.findById(id).map(orderMapper::orderToOrderDTO);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = {"orders", "pagedOrders"},
            allEntries = true)
    public OrderDTO createOrder(CreateOrderRequest request) {
        // Create new order entity
        Order order = new Order();
        order.setDescription(request.getDescription());

        // Set customer relationship
        Customer customer = customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.getCustomerId()));
        order.setCustomer(customer);

        // Set products relationship if provided
        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            List<Product> products = productRepository.findAllById(request.getProductIds());
            order.setProducts(products);
        }

        // Save the order
        Order savedOrder = orderRepository.save(order);

        return orderMapper.orderToOrderDTO(savedOrder);
    }
}
