package com.example.store.service;

import com.example.store.exception.ValidationException;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** The type Validation service. */
@Service
public class ValidationService {

    private static final int MIN_NAME_LENGTH = 1;
    private static final int MAX_NAME_LENGTH = 255;
    private static final int MIN_SEARCH_QUERY_LENGTH = 1;
    private static final int MAX_SEARCH_QUERY_LENGTH = 100;

    /**
     * Validates customer name.
     *
     * @param name the customer name to validate
     * @throws ValidationException if validation fails
     */
    public void validateCustomerName(String name) {
        if (!StringUtils.hasText(name)) {
            throw ValidationException.requiredField("name");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() < MIN_NAME_LENGTH) {
            throw ValidationException.invalidInput("name", "Name cannot be empty");
        }

        if (trimmedName.length() > MAX_NAME_LENGTH) {
            throw ValidationException.invalidInput("name", "Name cannot exceed " + MAX_NAME_LENGTH + " characters");
        }

        // Check for valid characters (letters, spaces, hyphens, apostrophes)
        if (!trimmedName.matches("^[a-zA-Z\\s\\-']+$")) {
            throw ValidationException.invalidInput(
                    "name", "Name can only contain letters, spaces, hyphens, and apostrophes");
        }
    }

    /**
     * Validates search query.
     *
     * @param query the search query to validate
     * @throws ValidationException if validation fails
     */
    public void validateSearchQuery(String query) {
        if (query == null) {
            return; // null queries are handled gracefully by returning all results
        }

        String trimmedQuery = query.trim();
        if (trimmedQuery.isEmpty()) {
            return; // empty queries are handled gracefully by returning all results
        }

        if (trimmedQuery.length() < MIN_SEARCH_QUERY_LENGTH) {
            throw ValidationException.invalidSearchQuery("Search query too short");
        }

        if (trimmedQuery.length() > MAX_SEARCH_QUERY_LENGTH) {
            throw ValidationException.invalidSearchQuery("Search query too long");
        }

        // Check for valid characters (letters, spaces, hyphens, apostrophes)
        if (!trimmedQuery.matches("^[a-zA-Z\\s\\-']+$")) {
            throw ValidationException.invalidSearchQuery("Search query contains invalid characters");
        }
    }

    /**
     * Validates customer ID.
     *
     * @param id the customer ID to validate
     * @throws ValidationException if validation fails
     */
    public void validateCustomerId(Long id) {
        if (id == null) {
            throw ValidationException.requiredField("id");
        }

        if (id <= 0) {
            throw ValidationException.invalidInput("id", "Customer ID must be positive");
        }
    }

    /**
     * Validates product description.
     *
     * @param description the product description to validate
     * @throws ValidationException if validation fails
     */
    public void validateProductDescription(String description) {
        if (!StringUtils.hasText(description)) {
            throw ValidationException.requiredField("description");
        }

        String trimmedDescription = description.trim();
        if (trimmedDescription.length() < MIN_NAME_LENGTH) {
            throw ValidationException.invalidInput("description", "Description cannot be empty");
        }

        if (trimmedDescription.length() > 500) {
            throw ValidationException.invalidInput("description", "Description cannot exceed 500 characters");
        }

        // Check for valid characters (letters, numbers, spaces, hyphens, apostrophes, periods, commas)
        if (!trimmedDescription.matches("^[a-zA-Z0-9\\s\\-'.]+$")) {
            throw ValidationException.invalidInput(
                    "description",
                    "Description can only contain letters, numbers, spaces, hyphens, apostrophes, periods, and commas");
        }
    }

    /**
     * Validates product ID.
     *
     * @param id the product ID to validate
     * @throws ValidationException if validation fails
     */
    public void validateProductId(Long id) {
        if (id == null) {
            throw ValidationException.requiredField("id");
        }

        if (id <= 0) {
            throw ValidationException.invalidInput("id", "Product ID must be positive");
        }
    }

    /**
     * Sanitizes input by trimming whitespace and converting to proper case.
     *
     * @param input the input to sanitize
     * @return sanitized input
     */
    public String sanitizeName(String input) {
        if (!StringUtils.hasText(input)) {
            return null;
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        // Convert to title case (first letter of each word capitalized)
        return trimmed.toLowerCase()
                .chars()
                .mapToObj(c -> (char) c)
                .reduce(
                        new StringBuilder(),
                        (sb, c) -> {
                            if (sb.length() == 0 || sb.charAt(sb.length() - 1) == ' ') {
                                sb.append(Character.toUpperCase(c));
                            } else {
                                sb.append(c);
                            }
                            return sb;
                        },
                        StringBuilder::append)
                .toString();
    }

    /**
     * Sanitizes product description by trimming whitespace and converting to proper case.
     *
     * @param input the input to sanitize
     * @return sanitized input
     */
    public String sanitizeDescription(String input) {
        if (!StringUtils.hasText(input)) {
            return null;
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        // Convert to title case (first letter of each word capitalized)
        return trimmed.toLowerCase()
                .chars()
                .mapToObj(c -> (char) c)
                .reduce(
                        new StringBuilder(),
                        (sb, c) -> {
                            if (sb.length() == 0 || sb.charAt(sb.length() - 1) == ' ') {
                                sb.append(Character.toUpperCase(c));
                            } else {
                                sb.append(c);
                            }
                            return sb;
                        },
                        StringBuilder::append)
                .toString();
    }
}
