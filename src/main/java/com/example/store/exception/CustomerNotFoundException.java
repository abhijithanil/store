package com.example.store.exception;

/** Exception thrown when a customer is not found. Follows SOLID principles by having a single responsibility. */
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static CustomerNotFoundException withId(Long id) {
        return new CustomerNotFoundException("Customer not found with ID: " + id);
    }

    public static CustomerNotFoundException withName(String name) {
        return new CustomerNotFoundException("Customer not found with name: " + name);
    }
}
