package com.example.store.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** The type Global exception handler test. */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private WebRequest webRequest;

    /** Sets up. */
    @BeforeEach
    void setUp() {
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    /** Should handle validation exceptions successfully. */
    @Test
    @DisplayName("Should handle validation exceptions successfully")
    void shouldHandleValidationExceptionsSuccessfully() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("customer", "name", "Name is required");
        FieldError globalError = new FieldError("customer", "global", "Customer validation failed");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError, globalError));

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid input data", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertNotNull(response.getBody().getValidationErrors());
        assertTrue(response.getBody().getValidationErrors().containsKey("name"));
        assertTrue(response.getBody().getValidationErrors().containsKey("global"));
    }

    /** Should handle customer not found exception successfully. */
    @Test
    @DisplayName("Should handle customer not found exception successfully")
    void shouldHandleCustomerNotFoundExceptionSuccessfully() {
        // Given
        CustomerNotFoundException exception = new CustomerNotFoundException("Customer not found with ID: 999");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCustomerNotFoundException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Customer not found with ID: 999", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertNull(response.getBody().getValidationErrors());
    }

    /** Should handle order not found exception successfully. */
    @Test
    @DisplayName("Should handle order not found exception successfully")
    void shouldHandleOrderNotFoundExceptionSuccessfully() {
        // Given
        OrderNotFoundException exception = new OrderNotFoundException("Order not found with ID: 999");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleOrderNotFoundException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Order not found with ID: 999", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertNull(response.getBody().getValidationErrors());
    }

    /** Should handle product not found exception successfully. */
    @Test
    @DisplayName("Should handle product not found exception successfully")
    void shouldHandleProductNotFoundExceptionSuccessfully() {
        // Given
        ProductNotFoundException exception = new ProductNotFoundException("Product not found with ID: 999");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleProductNotFoundException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product not found with ID: 999", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertNull(response.getBody().getValidationErrors());
    }

    /** Should handle validation exception successfully. */
    @Test
    @DisplayName("Should handle validation exception successfully")
    void shouldHandleValidationExceptionSuccessfully() {
        // Given
        ValidationException exception = new ValidationException("Invalid input provided");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid input provided", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertNull(response.getBody().getValidationErrors());
    }

    /** Should handle illegal argument exception successfully. */
    @Test
    @DisplayName("Should handle illegal argument exception successfully")
    void shouldHandleIllegalArgumentExceptionSuccessfully() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument provided");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid argument provided", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertNull(response.getBody().getValidationErrors());
    }

    /** Should handle runtime exception successfully. */
    @Test
    @DisplayName("Should handle runtime exception successfully")
    void shouldHandleRuntimeExceptionSuccessfully() {
        // Given
        RuntimeException exception = new RuntimeException("Internal server error");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleRuntimeException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertNull(response.getBody().getValidationErrors());
    }

    /** Should handle generic exception successfully. */
    @Test
    @DisplayName("Should handle generic exception successfully")
    void shouldHandleGenericExceptionSuccessfully() {
        // Given
        Exception exception = new Exception("Generic error occurred");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertNull(response.getBody().getValidationErrors());
    }

    /** Should handle validation exceptions with multiple field errors. */
    @Test
    @DisplayName("Should handle validation exceptions with multiple field errors")
    void shouldHandleValidationExceptionsWithMultipleFieldErrors() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("customer", "name", "Name is required");
        FieldError fieldError2 = new FieldError("customer", "email", "Email is invalid");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid input data", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertNotNull(response.getBody().getValidationErrors());
        assertTrue(response.getBody().getValidationErrors().containsKey("name"));
        assertTrue(response.getBody().getValidationErrors().containsKey("email"));
        assertEquals("Name is required", response.getBody().getValidationErrors().get("name"));
        assertEquals("Email is invalid", response.getBody().getValidationErrors().get("email"));
    }

    /** Should handle validation exceptions with only global errors. */
    @Test
    @DisplayName("Should handle validation exceptions with only global errors")
    void shouldHandleValidationExceptionsWithOnlyGlobalErrors() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError globalError = new FieldError("customer", "global", "Customer validation failed");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(globalError));

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid input data", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertNotNull(response.getBody().getValidationErrors());
        assertTrue(response.getBody().getValidationErrors().containsKey("global"));
        assertEquals("Customer validation failed", response.getBody().getValidationErrors().get("global"));
    }

    /** Should handle validation exceptions with no errors. */
    @Test
    @DisplayName("Should handle validation exceptions with no errors")
    void shouldHandleValidationExceptionsWithNoErrors() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList());

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid input data", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertNotNull(response.getBody().getValidationErrors());
        assertTrue(response.getBody().getValidationErrors().isEmpty());
    }
}
