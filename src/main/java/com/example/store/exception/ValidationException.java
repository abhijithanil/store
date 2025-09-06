package com.example.store.exception;

/**
 * Exception thrown when validation fails. Follows SOLID principles by having a single responsibility for validation
 * errors.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ValidationException invalidInput(String field, String value) {
        return new ValidationException("Invalid input for field '" + field + "': " + value);
    }

    public static ValidationException requiredField(String field) {
        return new ValidationException("Required field '" + field + "' is missing or empty");
    }

    public static ValidationException invalidSearchQuery(String query) {
        return new ValidationException("Invalid search query: " + query);
    }
}
