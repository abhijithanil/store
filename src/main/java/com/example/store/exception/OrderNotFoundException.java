package com.example.store.exception;

/** Order not found exception. */
public class OrderNotFoundException extends RuntimeException {

    /**
     * Instantiates a new Order not found exception.
     *
     * @param message the message
     */
    public OrderNotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Order not found exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * With id order not found exception.
     *
     * @param id the id
     * @return the order not found exception
     */
    public static OrderNotFoundException withId(Long id) {
        return new OrderNotFoundException("Order not found with ID: " + id);
    }
}
