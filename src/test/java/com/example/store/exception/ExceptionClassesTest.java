package com.example.store.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/** The type Exception classes test. */
@ExtendWith(MockitoExtension.class)
@DisplayName("Exception Classes Tests")
class ExceptionClassesTest {

    /** Should create customer not found exception with message. */
    @Test
    @DisplayName("Should create CustomerNotFoundException with message")
    void shouldCreateCustomerNotFoundExceptionWithMessage() {
        // Given
        String message = "Customer not found";

        // When
        CustomerNotFoundException exception = new CustomerNotFoundException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /** Should create customer not found exception with message and cause. */
    @Test
    @DisplayName("Should create CustomerNotFoundException with message and cause")
    void shouldCreateCustomerNotFoundExceptionWithMessageAndCause() {
        // Given
        String message = "Customer not found";
        Throwable cause = new RuntimeException("Database error");

        // When
        CustomerNotFoundException exception = new CustomerNotFoundException(message, cause);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    /** Should create customer not found exception with id using withId method. */
    @Test
    @DisplayName("Should create CustomerNotFoundException with ID using withId method")
    void shouldCreateCustomerNotFoundExceptionWithIdUsingWithIdMethod() {
        // Given
        Long customerId = 999L;

        // When
        CustomerNotFoundException exception = CustomerNotFoundException.withId(customerId);

        // Then
        assertNotNull(exception);
        assertEquals("Customer not found with ID: 999", exception.getMessage());
        assertNull(exception.getCause());
    }

    /** Should create customer not found exception with name using withName method. */
    @Test
    @DisplayName("Should create CustomerNotFoundException with name using withName method")
    void shouldCreateCustomerNotFoundExceptionWithNameUsingWithNameMethod() {
        // Given
        String customerName = "John Doe";

        // When
        CustomerNotFoundException exception = CustomerNotFoundException.withName(customerName);

        // Then
        assertNotNull(exception);
        assertEquals("Customer not found with name: John Doe", exception.getMessage());
        assertNull(exception.getCause());
    }

    /** Should create order not found exception with message. */
    @Test
    @DisplayName("Should create OrderNotFoundException with message")
    void shouldCreateOrderNotFoundExceptionWithMessage() {
        // Given
        String message = "Order not found";

        // When
        OrderNotFoundException exception = new OrderNotFoundException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /** Should create order not found exception with message and cause. */
    @Test
    @DisplayName("Should create OrderNotFoundException with message and cause")
    void shouldCreateOrderNotFoundExceptionWithMessageAndCause() {
        // Given
        String message = "Order not found";
        Throwable cause = new RuntimeException("Database error");

        // When
        OrderNotFoundException exception = new OrderNotFoundException(message, cause);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    /** Should create order not found exception with id using withId method. */
    @Test
    @DisplayName("Should create OrderNotFoundException with ID using withId method")
    void shouldCreateOrderNotFoundExceptionWithIdUsingWithIdMethod() {
        // Given
        Long orderId = 999L;

        // When
        OrderNotFoundException exception = OrderNotFoundException.withId(orderId);

        // Then
        assertNotNull(exception);
        assertEquals("Order not found with ID: 999", exception.getMessage());
        assertNull(exception.getCause());
    }

    /** Should create product not found exception with message. */
    @Test
    @DisplayName("Should create ProductNotFoundException with message")
    void shouldCreateProductNotFoundExceptionWithMessage() {
        // Given
        String message = "Product not found";

        // When
        ProductNotFoundException exception = new ProductNotFoundException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /** Should create product not found exception with message and cause. */
    @Test
    @DisplayName("Should create ProductNotFoundException with message and cause")
    void shouldCreateProductNotFoundExceptionWithMessageAndCause() {
        // Given
        String message = "Product not found";
        Throwable cause = new RuntimeException("Database error");

        // When
        ProductNotFoundException exception = new ProductNotFoundException(message, cause);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    /** Should create product not found exception with id using withId method. */
    @Test
    @DisplayName("Should create ProductNotFoundException with ID using withId method")
    void shouldCreateProductNotFoundExceptionWithIdUsingWithIdMethod() {
        // Given
        Long productId = 999L;

        // When
        ProductNotFoundException exception = ProductNotFoundException.withId(productId);

        // Then
        assertNotNull(exception);
        assertEquals("Product not found with ID: 999", exception.getMessage());
        assertNull(exception.getCause());
    }

    /** Should create validation exception with message. */
    @Test
    @DisplayName("Should create ValidationException with message")
    void shouldCreateValidationExceptionWithMessage() {
        // Given
        String message = "Validation failed";

        // When
        ValidationException exception = new ValidationException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /** Should create validation exception with message and cause. */
    @Test
    @DisplayName("Should create ValidationException with message and cause")
    void shouldCreateValidationExceptionWithMessageAndCause() {
        // Given
        String message = "Validation failed";
        Throwable cause = new IllegalArgumentException("Invalid input");

        // When
        ValidationException exception = new ValidationException(message, cause);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    /** Should create validation exception with invalid input using invalidInput method. */
    @Test
    @DisplayName("Should create ValidationException with invalid input using invalidInput method")
    void shouldCreateValidationExceptionWithInvalidInputUsingInvalidInputMethod() {
        // Given
        String field = "name";
        String value = "invalid";

        // When
        ValidationException exception = ValidationException.invalidInput(field, value);

        // Then
        assertNotNull(exception);
        assertEquals("Invalid input for field 'name': invalid", exception.getMessage());
        assertNull(exception.getCause());
    }

    /** Should create validation exception with required field using requiredField method. */
    @Test
    @DisplayName("Should create ValidationException with required field using requiredField method")
    void shouldCreateValidationExceptionWithRequiredFieldUsingRequiredFieldMethod() {
        // Given
        String field = "email";

        // When
        ValidationException exception = ValidationException.requiredField(field);

        // Then
        assertNotNull(exception);
        assertEquals("Required field 'email' is missing or empty", exception.getMessage());
        assertNull(exception.getCause());
    }

    /** Should create validation exception with invalid search query using invalidSearchQuery method. */
    @Test
    @DisplayName("Should create ValidationException with invalid search query using invalidSearchQuery method")
    void shouldCreateValidationExceptionWithInvalidSearchQueryUsingInvalidSearchQueryMethod() {
        // Given
        String query = "a";

        // When
        ValidationException exception = ValidationException.invalidSearchQuery(query);

        // Then
        assertNotNull(exception);
        assertEquals("Invalid search query: a", exception.getMessage());
        assertNull(exception.getCause());
    }

    /** Should test exception inheritance hierarchy. */
    @Test
    @DisplayName("Should test exception inheritance hierarchy")
    void shouldTestExceptionInheritanceHierarchy() {
        // Given
        String message = "Test exception";

        // When
        CustomerNotFoundException customerException = new CustomerNotFoundException(message);
        OrderNotFoundException orderException = new OrderNotFoundException(message);
        ProductNotFoundException productException = new ProductNotFoundException(message);
        ValidationException validationException = new ValidationException(message);

        // Then
        assertTrue(customerException instanceof RuntimeException);
        assertTrue(orderException instanceof RuntimeException);
        assertTrue(productException instanceof RuntimeException);
        assertTrue(validationException instanceof RuntimeException);
    }

    /** Should test exception with null message. */
    @Test
    @DisplayName("Should test exception with null message")
    void shouldTestExceptionWithNullMessage() {
        // Given
        String message = null;

        // When
        CustomerNotFoundException exception = new CustomerNotFoundException(message);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    /** Should test exception with empty message. */
    @Test
    @DisplayName("Should test exception with empty message")
    void shouldTestExceptionWithEmptyMessage() {
        // Given
        String message = "";

        // When
        OrderNotFoundException exception = new OrderNotFoundException(message);

        // Then
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }
}
