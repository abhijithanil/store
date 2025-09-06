package com.example.store.exception;

/**
 * Product not found exception.
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Instantiates a new Product not found exception.
     *
     * @param message the message
     */
    public ProductNotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Product not found exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * With id product not found exception.
     *
     * @param id the id
     * @return the product not found exception
     */
    public static ProductNotFoundException withId(Long id) {
        return new ProductNotFoundException("Product not found with ID: " + id);
    }
}
