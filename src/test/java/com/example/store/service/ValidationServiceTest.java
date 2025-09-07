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

    /** Should validate valid search query. */
    @Test
    @DisplayName("Should validate valid search query")
    void shouldValidateValidSearchQuery() {
        // Given
        String validQuery = "laptop computer";

        // When & Then
        assertDoesNotThrow(() -> validationService.validateSearchQuery(validQuery));
    }

    /** Should handle null search query gracefully. */
    @Test
    @DisplayName("Should handle null search query gracefully")
    void shouldHandleNullSearchQueryGracefully() {
        // Given
        String nullQuery = null;

        // When & Then
        assertDoesNotThrow(() -> validationService.validateSearchQuery(nullQuery));
    }

    /** Should handle empty search query gracefully. */
    @Test
    @DisplayName("Should handle empty search query gracefully")
    void shouldHandleEmptySearchQueryGracefully() {
        // Given
        String emptyQuery = "";

        // When & Then
        assertDoesNotThrow(() -> validationService.validateSearchQuery(emptyQuery));
    }

    /** Should handle whitespace only search query gracefully. */
    @Test
    @DisplayName("Should handle whitespace-only search query gracefully")
    void shouldHandleWhitespaceOnlySearchQueryGracefully() {
        // Given
        String whitespaceQuery = "   ";

        // When & Then
        assertDoesNotThrow(() -> validationService.validateSearchQuery(whitespaceQuery));
    }

    /** Should handle single character search query gracefully. */
    @Test
    @DisplayName("Should handle single character search query gracefully")
    void shouldHandleSingleCharacterSearchQueryGracefully() {
        // Given
        String shortQuery = "a";

        // When & Then
        assertDoesNotThrow(() -> validationService.validateSearchQuery(shortQuery));
    }

    /** Should throw exception for search query with invalid characters. */
    @Test
    @DisplayName("Should throw exception for search query with invalid characters")
    void shouldThrowExceptionForSearchQueryWithInvalidCharacters() {
        // Given
        String invalidQuery = "laptop123";

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> validationService.validateSearchQuery(invalidQuery));
        assertEquals("Invalid search query: Search query contains invalid characters", exception.getMessage());
    }

    /** Should validate valid product description. */
    @Test
    @DisplayName("Should validate valid product description")
    void shouldValidateValidProductDescription() {
        // Given
        String validDescription = "High-quality laptop computer";

        // When & Then
        assertDoesNotThrow(() -> validationService.validateProductDescription(validDescription));
    }

    /** Should throw exception for null product description. */
    @Test
    @DisplayName("Should throw exception for null product description")
    void shouldThrowExceptionForNullProductDescription() {
        // Given
        String nullDescription = null;

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class, () -> validationService.validateProductDescription(nullDescription));
        assertEquals("Required field 'description' is missing or empty", exception.getMessage());
    }

    /** Should throw exception for empty product description. */
    @Test
    @DisplayName("Should throw exception for empty product description")
    void shouldThrowExceptionForEmptyProductDescription() {
        // Given
        String emptyDescription = "";

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class, () -> validationService.validateProductDescription(emptyDescription));
        assertEquals("Required field 'description' is missing or empty", exception.getMessage());
    }

    /** Should throw exception for whitespace only product description. */
    @Test
    @DisplayName("Should throw exception for whitespace-only product description")
    void shouldThrowExceptionForWhitespaceOnlyProductDescription() {
        // Given
        String whitespaceDescription = "   ";

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class, () -> validationService.validateProductDescription(whitespaceDescription));
        assertEquals("Required field 'description' is missing or empty", exception.getMessage());
    }

    /** Should throw exception for product description with invalid characters. */
    @Test
    @DisplayName("Should throw exception for product description with invalid characters")
    void shouldThrowExceptionForProductDescriptionWithInvalidCharacters() {
        // Given
        String invalidDescription = "Laptop@#$%";

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class, () -> validationService.validateProductDescription(invalidDescription));
        assertEquals(
                "Invalid input for field 'description': Description can only contain letters, numbers, spaces, hyphens, apostrophes, periods, and commas",
                exception.getMessage());
    }

    /** Should validate valid product id. */
    @Test
    @DisplayName("Should validate valid product ID")
    void shouldValidateValidProductId() {
        // Given
        Long validId = 1L;

        // When & Then
        assertDoesNotThrow(() -> validationService.validateProductId(validId));
    }

    /** Should throw exception for null product id. */
    @Test
    @DisplayName("Should throw exception for null product ID")
    void shouldThrowExceptionForNullProductId() {
        // Given
        Long nullId = null;

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> validationService.validateProductId(nullId));
        assertEquals("Required field 'id' is missing or empty", exception.getMessage());
    }

    /** Should throw exception for negative product id. */
    @Test
    @DisplayName("Should throw exception for negative product ID")
    void shouldThrowExceptionForNegativeProductId() {
        // Given
        Long negativeId = -1L;

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> validationService.validateProductId(negativeId));
        assertEquals("Invalid input for field 'id': Product ID must be positive", exception.getMessage());
    }

    /** Should throw exception for zero product id. */
    @Test
    @DisplayName("Should throw exception for zero product ID")
    void shouldThrowExceptionForZeroProductId() {
        // Given
        Long zeroId = 0L;

        // When & Then
        ValidationException exception =
                assertThrows(ValidationException.class, () -> validationService.validateProductId(zeroId));
        assertEquals("Invalid input for field 'id': Product ID must be positive", exception.getMessage());
    }

    /** Should sanitize description correctly. */
    @Test
    @DisplayName("Should sanitize description correctly")
    void shouldSanitizeDescriptionCorrectly() {
        // Given
        String input = "  laptop computer  ";

        // When
        String result = validationService.sanitizeDescription(input);

        // Then
        assertEquals("Laptop Computer", result);
    }

    /** Should return null for null description input. */
    @Test
    @DisplayName("Should return null for null description input")
    void shouldReturnNullForNullDescriptionInput() {
        // Given
        String nullInput = null;

        // When
        String result = validationService.sanitizeDescription(nullInput);

        // Then
        assertNull(result);
    }

    /** Should return null for empty description input. */
    @Test
    @DisplayName("Should return null for empty description input")
    void shouldReturnNullForEmptyDescriptionInput() {
        // Given
        String emptyInput = "";

        // When
        String result = validationService.sanitizeDescription(emptyInput);

        // Then
        assertNull(result);
    }

    /** Should return null for whitespace only description input. */
    @Test
    @DisplayName("Should return null for whitespace-only description input")
    void shouldReturnNullForWhitespaceOnlyDescriptionInput() {
        // Given
        String whitespaceInput = "   ";

        // When
        String result = validationService.sanitizeDescription(whitespaceInput);

        // Then
        assertNull(result);
    }
}
