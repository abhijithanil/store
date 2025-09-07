package com.example.store.exception;

/** Validation exception. */
public class ValidationException extends RuntimeException {

    /**
     * Instantiates a new Validation exception.
     *
     * @param message the message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Validation exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Invalid input validation exception.
     *
     * @param field the field
     * @param value the value
     * @return the validation exception
     */
    public static ValidationException invalidInput(String field, String value) {
        return new ValidationException("Invalid input for field '" + field + "': " + value);
    }

    /**
     * Required field validation exception.
     *
     * @param field the field
     * @return the validation exception
     */
    public static ValidationException requiredField(String field) {
        return new ValidationException("Required field '" + field + "' is missing or empty");
    }

    /**
     * Invalid search query validation exception.
     *
     * @param query the query
     * @return the validation exception
     */
    public static ValidationException invalidSearchQuery(String query) {
        return new ValidationException("Invalid search query: " + query);
    }
}
