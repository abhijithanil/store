package com.example.store.service;

import com.example.store.exception.ValidationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/** The type Validation service test. */
@ExtendWith(MockitoExtension.class)
@DisplayName("ValidationService Tests")
class ValidationServiceTest {

    @InjectMocks
    private ValidationService validationService;

    /** Should validate valid customer name. */
    @Test
    @DisplayName("Should validate valid customer name")
    void shouldValidateValidCustomerName() {
        // Given
        String validName = "John Doe";

        // When & Then
        assertDoesNotThrow(() -> validationService.validateCustomerName(validName));
    }

    /** Should throw exception for null customer name. */
    @Test
    @DisplayName("Should throw exception for null customer name")
    void shouldThrowExceptionForNullCustomerName() {
        // Given
        String nullName = null;

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> validationService.validateCustomerName(nullName));
        assertEquals("Required field 'name' is missing or empty", exception.getMessage());
    }

    /** Should throw exception for empty customer name. */
    @Test
    @DisplayName("Should throw exception for empty customer name")
    void shouldThrowExceptionForEmptyCustomerName() {
        // Given
        String emptyName = "";

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> validationService.validateCustomerName(emptyName));
        assertEquals("Required field 'name' is missing or empty", exception.getMessage());
    }

    /** Should throw exception for whitespace only customer name. */
    @Test
    @DisplayName("Should throw exception for whitespace-only customer name")
    void shouldThrowExceptionForWhitespaceOnlyCustomerName() {
        // Given
        String whitespaceName = "   ";

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> validationService.validateCustomerName(whitespaceName));
        assertEquals("Required field 'name' is missing or empty", exception.getMessage());
    }

    /** Should throw exception for customer name with invalid characters. */
    @Test
    @DisplayName("Should throw exception for customer name with invalid characters")
    void shouldThrowExceptionForCustomerNameWithInvalidCharacters() {
        // Given
        String invalidName = "John123";

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> validationService.validateCustomerName(invalidName));
        assertEquals(
                "Invalid input for field 'name': Name can only contain letters, spaces, hyphens, and apostrophes",
                exception.getMessage());
    }

    /** Should validate customer name with hyphen. */
    @Test
    @DisplayName("Should validate customer name with hyphen")
    void shouldValidateCustomerNameWithHyphen() {
        // Given
        String nameWithHyphen = "Mary-Jane";

        // When & Then
        assertDoesNotThrow(() -> validationService.validateCustomerName(nameWithHyphen));
    }

    /** Should validate customer name with apostrophe. */
    @Test
    @DisplayName("Should validate customer name with apostrophe")
    void shouldValidateCustomerNameWithApostrophe() {
        // Given
        String nameWithApostrophe = "O'Connor";

        // When & Then
        assertDoesNotThrow(() -> validationService.validateCustomerName(nameWithApostrophe));
    }

    /** Should validate valid customer id. */
    @Test
    @DisplayName("Should validate valid customer ID")
    void shouldValidateValidCustomerId() {
        // Given
        Long validId = 1L;

        // When & Then
        assertDoesNotThrow(() -> validationService.validateCustomerId(validId));
    }

    /** Should throw exception for null customer id. */
    @Test
    @DisplayName("Should throw exception for null customer ID")
    void shouldThrowExceptionForNullCustomerId() {
        // Given
        Long nullId = null;

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> validationService.validateCustomerId(nullId));
        assertEquals("Required field 'id' is missing or empty", exception.getMessage());
    }

    /** Should throw exception for negative customer id. */
    @Test
    @DisplayName("Should throw exception for negative customer ID")
    void shouldThrowExceptionForNegativeCustomerId() {
        // Given
        Long negativeId = -1L;

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> validationService.validateCustomerId(negativeId));
        assertEquals("Invalid input for field 'id': Customer ID must be positive", exception.getMessage());
    }

    /** Should throw exception for zero customer id. */
    @Test
    @DisplayName("Should throw exception for zero customer ID")
    void shouldThrowExceptionForZeroCustomerId() {
        // Given
        Long zeroId = 0L;

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> validationService.validateCustomerId(zeroId));
        assertEquals("Invalid input for field 'id': Customer ID must be positive", exception.getMessage());
    }

    /** Should sanitize name correctly. */
    @Test
    @DisplayName("Should sanitize name correctly")
    void shouldSanitizeNameCorrectly() {
        // Given
        String input = "  john doe  ";

        // When
        String result = validationService.sanitizeName(input);

        // Then
        assertEquals("John Doe", result);
    }

    /** Should return null for null input. */
    @Test
    @DisplayName("Should return null for null input")
    void shouldReturnNullForNullInput() {
        // Given
        String nullInput = null;

        // When
        String result = validationService.sanitizeName(nullInput);

        // Then
        assertNull(result);
    }

    /** Should return null for empty input. */
    @Test
    @DisplayName("Should return null for empty input")
    void shouldReturnNullForEmptyInput() {
        // Given
        String emptyInput = "";

        // When
        String result = validationService.sanitizeName(emptyInput);

        // Then
        assertNull(result);
    }

    /** Should return null for whitespace only input. */
    @Test
    @DisplayName("Should return null for whitespace-only input")
    void shouldReturnNullForWhitespaceOnlyInput() {
        // Given
        String whitespaceInput = "   ";

        // When
        String result = validationService.sanitizeName(whitespaceInput);

        // Then
        assertNull(result);
    }
}
