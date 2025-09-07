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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** The type Order service impl test. */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderDTO orderDTO;
    private Customer customer;
    private Product product;
    private CreateOrderRequest createOrderRequest;

    /** Sets up. */
    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        product = new Product();
        product.setId(1L);
        product.setDescription("Laptop Computer");

        order = new Order();
        order.setId(1L);
        order.setDescription("Order for laptop");
        order.setCustomer(customer);
        order.setProducts(Arrays.asList(product));

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setDescription("Order for laptop");

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setDescription("Order for laptop");
        createOrderRequest.setCustomerId(1L);
        createOrderRequest.setProductIds(Arrays.asList(1L));
    }

    /** Should get all orders with pagination successfully. */
    @Test
    @DisplayName("Should get all orders with pagination successfully")
    void shouldGetAllOrdersWithPaginationSuccessfully() {
        // Given
        List<Order> orders = Arrays.asList(order);
        Page<Order> orderPage = new PageImpl<>(orders);
        List<OrderDTO> orderDTOs = Arrays.asList(orderDTO);

        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.orderToOrderDTO(any(Order.class))).thenReturn(orderDTO);

        // When
        PagedResponse<OrderDTO> result = orderService.getAllOrders(0, 10, "id", "asc");

        // Then
        assertNotNull(result);
        verify(orderRepository).findAll(any(Pageable.class));
        verify(orderMapper, atLeastOnce()).orderToOrderDTO(any(Order.class));
    }

    /** Should get all orders successfully. */
    @Test
    @DisplayName("Should get all orders successfully")
    void shouldGetAllOrdersSuccessfully() {
        // Given
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findAll()).thenReturn(orders);
        List<OrderDTO> orderDTOs = Arrays.asList(orderDTO);
        when(orderMapper.ordersToOrderDTOs(orders)).thenReturn(orderDTOs);

        // When
        List<OrderDTO> result = orderService.getAllOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderDTO, result.get(0));
        verify(orderRepository).findAll();
        verify(orderMapper).ordersToOrderDTOs(orders);
    }

    /** Should get order by id successfully. */
    @Test
    @DisplayName("Should get order by ID successfully")
    void shouldGetOrderByIdSuccessfully() {
        // Given
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        // When
        Optional<OrderDTO> result = orderService.getOrderById(orderId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(orderDTO, result.get());
        verify(orderRepository).findById(orderId);
        verify(orderMapper).orderToOrderDTO(order);
    }

    /** Should return empty optional when order not found by id. */
    @Test
    @DisplayName("Should return empty optional when order not found by ID")
    void shouldReturnEmptyOptionalWhenOrderNotFoundById() {
        // Given
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When
        Optional<OrderDTO> result = orderService.getOrderById(orderId);

        // Then
        assertFalse(result.isPresent());
        verify(orderRepository).findById(orderId);
        verify(orderMapper, never()).orderToOrderDTO(any());
    }

    /** Should create order successfully. */
    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrderSuccessfully() {
        // Given
        Order savedOrder = new Order();
        savedOrder.setId(2L);
        savedOrder.setDescription("Order for laptop");
        savedOrder.setCustomer(customer);
        savedOrder.setProducts(Arrays.asList(product));

        OrderDTO savedOrderDTO = new OrderDTO();
        savedOrderDTO.setId(2L);
        savedOrderDTO.setDescription("Order for laptop");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(product));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.orderToOrderDTO(savedOrder)).thenReturn(savedOrderDTO);

        // When
        OrderDTO result = orderService.createOrder(createOrderRequest);

        // Then
        assertNotNull(result);
        assertEquals(savedOrderDTO, result);
        verify(customerRepository).findById(1L);
        verify(productRepository).findAllById(Arrays.asList(1L));
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).orderToOrderDTO(savedOrder);
    }

    /** Should create order without products successfully. */
    @Test
    @DisplayName("Should create order without products successfully")
    void shouldCreateOrderWithoutProductsSuccessfully() {
        // Given
        CreateOrderRequest requestWithoutProducts = new CreateOrderRequest();
        requestWithoutProducts.setDescription("Order without products");
        requestWithoutProducts.setCustomerId(1L);
        requestWithoutProducts.setProductIds(null);

        Order savedOrder = new Order();
        savedOrder.setId(2L);
        savedOrder.setDescription("Order without products");
        savedOrder.setCustomer(customer);

        OrderDTO savedOrderDTO = new OrderDTO();
        savedOrderDTO.setId(2L);
        savedOrderDTO.setDescription("Order without products");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.orderToOrderDTO(savedOrder)).thenReturn(savedOrderDTO);

        // When
        OrderDTO result = orderService.createOrder(requestWithoutProducts);

        // Then
        assertNotNull(result);
        assertEquals(savedOrderDTO, result);
        verify(customerRepository).findById(1L);
        verify(productRepository, never()).findAllById(any());
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).orderToOrderDTO(savedOrder);
    }

    /** Should create order with empty product list successfully. */
    @Test
    @DisplayName("Should create order with empty product list successfully")
    void shouldCreateOrderWithEmptyProductListSuccessfully() {
        // Given
        CreateOrderRequest requestWithEmptyProducts = new CreateOrderRequest();
        requestWithEmptyProducts.setDescription("Order with empty products");
        requestWithEmptyProducts.setCustomerId(1L);
        requestWithEmptyProducts.setProductIds(Arrays.asList());

        Order savedOrder = new Order();
        savedOrder.setId(2L);
        savedOrder.setDescription("Order with empty products");
        savedOrder.setCustomer(customer);

        OrderDTO savedOrderDTO = new OrderDTO();
        savedOrderDTO.setId(2L);
        savedOrderDTO.setDescription("Order with empty products");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.orderToOrderDTO(savedOrder)).thenReturn(savedOrderDTO);

        // When
        OrderDTO result = orderService.createOrder(requestWithEmptyProducts);

        // Then
        assertNotNull(result);
        assertEquals(savedOrderDTO, result);
        verify(customerRepository).findById(1L);
        verify(productRepository, never()).findAllById(any());
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).orderToOrderDTO(savedOrder);
    }

    /** Should throw runtime exception when customer not found. */
    @Test
    @DisplayName("Should throw RuntimeException when customer not found")
    void shouldThrowRuntimeExceptionWhenCustomerNotFound() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> orderService.createOrder(createOrderRequest));
        assertEquals("Customer not found with ID: 1", exception.getMessage());
        verify(customerRepository).findById(1L);
        verify(productRepository, never()).findAllById(any());
        verify(orderRepository, never()).save(any());
    }

    /** Should handle repository exception in get all orders gracefully. */
    @Test
    @DisplayName("Should handle repository exception in get all orders gracefully")
    void shouldHandleRepositoryExceptionInGetAllOrdersGracefully() {
        // Given
        when(orderRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.getAllOrders());
        assertEquals("Database connection failed", exception.getMessage());
        verify(orderRepository).findAll();
    }

    /** Should handle repository exception in get order by id gracefully. */
    @Test
    @DisplayName("Should handle repository exception in get order by id gracefully")
    void shouldHandleRepositoryExceptionInGetOrderByIdGracefully() {
        // Given
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.getOrderById(orderId));
        assertEquals("Database connection failed", exception.getMessage());
        verify(orderRepository).findById(orderId);
    }

    /** Should handle repository exception in create order gracefully. */
    @Test
    @DisplayName("Should handle repository exception in create order gracefully")
    void shouldHandleRepositoryExceptionInCreateOrderGracefully() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(product));
        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> orderService.createOrder(createOrderRequest));
        assertEquals("Database connection failed", exception.getMessage());
        verify(customerRepository).findById(1L);
        verify(productRepository).findAllById(Arrays.asList(1L));
        verify(orderRepository).save(any(Order.class));
    }

    /** Should handle repository exception in get all orders with pagination gracefully. */
    @Test
    @DisplayName("Should handle repository exception in get all orders with pagination gracefully")
    void shouldHandleRepositoryExceptionInGetAllOrdersWithPaginationGracefully() {
        // Given
        when(orderRepository.findAll(any(Pageable.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> orderService.getAllOrders(0, 10, "id", "asc"));
        assertEquals("Database connection failed", exception.getMessage());
        verify(orderRepository).findAll(any(Pageable.class));
    }
}
