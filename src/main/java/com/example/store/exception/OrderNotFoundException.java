package com.example.store.exception;

/** Exception thrown when an order is not found. Follows SOLID principles by having a single responsibility. */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static OrderNotFoundException withId(Long id) {
        return new OrderNotFoundException("Order not found with ID: " + id);
    }
}
