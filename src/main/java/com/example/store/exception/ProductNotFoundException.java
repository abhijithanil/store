package com.example.store.exception;

/** Exception thrown when a product is not found. Follows SOLID principles by having a single responsibility. */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ProductNotFoundException withId(Long id) {
        return new ProductNotFoundException("Product not found with ID: " + id);
    }
}
