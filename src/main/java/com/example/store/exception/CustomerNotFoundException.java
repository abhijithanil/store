package com.example.store.exception;

/**
 * Customer not found exception.
 */
public class CustomerNotFoundException extends RuntimeException {

    /**
     * Instantiates a new Customer not found exception.
     *
     * @param message the message
     */
    public CustomerNotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Customer not found exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * With id customer not found exception.
     *
     * @param id the id
     * @return the customer not found exception
     */
    public static CustomerNotFoundException withId(Long id) {
        return new CustomerNotFoundException("Customer not found with ID: " + id);
    }

    /**
     * With name customer not found exception.
     *
     * @param name the name
     * @return the customer not found exception
     */
    public static CustomerNotFoundException withName(String name) {
        return new CustomerNotFoundException("Customer not found with name: " + name);
    }
}
