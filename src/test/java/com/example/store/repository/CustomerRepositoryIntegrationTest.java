package com.example.store.repository;

import com.example.store.entity.Customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Integration tests for CustomerRepository using H2 database. Tests actual database operations and queries. */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CustomerRepository Integration Tests")
class CustomerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer1;
    private Customer customer2;
    private Customer customer3;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        customerRepository.deleteAll();

        // Create test customers
        customer1 = new Customer();
        customer1.setName("John Doe");

        customer2 = new Customer();
        customer2.setName("Jane Smith");

        customer3 = new Customer();
        customer3.setName("Bob Johnson");

        // Save customers to database
        entityManager.persistAndFlush(customer1);
        entityManager.persistAndFlush(customer2);
        entityManager.persistAndFlush(customer3);
        entityManager.clear();
    }

    @Test
    @DisplayName("Should find all customers")
    void shouldFindAllCustomers() {
        // When
        List<Customer> customers = customerRepository.findAll();

        // Then
        assertNotNull(customers);
        assertEquals(3, customers.size());
        assertTrue(customers.stream().anyMatch(c -> "John Doe".equals(c.getName())));
        assertTrue(customers.stream().anyMatch(c -> "Jane Smith".equals(c.getName())));
        assertTrue(customers.stream().anyMatch(c -> "Bob Johnson".equals(c.getName())));
    }

    @Test
    @DisplayName("Should find customer by ID")
    void shouldFindCustomerById() {
        // When
        var customer = customerRepository.findById(customer1.getId());

        // Then
        assertTrue(customer.isPresent());
        assertEquals("John Doe", customer.get().getName());
        assertEquals(customer1.getId(), customer.get().getId());
    }

    @Test
    @DisplayName("Should return empty when customer not found by ID")
    void shouldReturnEmptyWhenCustomerNotFoundById() {
        // When
        var customer = customerRepository.findById(999L);

        // Then
        assertFalse(customer.isPresent());
    }

    @Test
    @DisplayName("Should save new customer")
    void shouldSaveNewCustomer() {
        // Given
        Customer newCustomer = new Customer();
        newCustomer.setName("Alice Brown");

        // When
        Customer savedCustomer = customerRepository.save(newCustomer);

        // Then
        assertNotNull(savedCustomer.getId());
        assertEquals("Alice Brown", savedCustomer.getName());

        // Verify it's actually saved in database
        var foundCustomer = customerRepository.findById(savedCustomer.getId());
        assertTrue(foundCustomer.isPresent());
        assertEquals("Alice Brown", foundCustomer.get().getName());
    }

    @Test
    @DisplayName("Should update existing customer")
    void shouldUpdateExistingCustomer() {
        // Given
        customer1.setName("John Updated");

        // When
        Customer updatedCustomer = customerRepository.save(customer1);

        // Then
        assertEquals("John Updated", updatedCustomer.getName());
        assertEquals(customer1.getId(), updatedCustomer.getId());

        // Verify it's actually updated in database
        var foundCustomer = customerRepository.findById(customer1.getId());
        assertTrue(foundCustomer.isPresent());
        assertEquals("John Updated", foundCustomer.get().getName());
    }

    @Test
    @DisplayName("Should delete customer by ID")
    void shouldDeleteCustomerById() {
        // Given
        Long customerId = customer1.getId();

        // When
        customerRepository.deleteById(customerId);

        // Then
        var foundCustomer = customerRepository.findById(customerId);
        assertFalse(foundCustomer.isPresent());
    }

    @Test
    @DisplayName("Should check if customer exists by ID")
    void shouldCheckIfCustomerExistsById() {
        // When & Then
        assertTrue(customerRepository.existsById(customer1.getId()));
        assertTrue(customerRepository.existsById(customer2.getId()));
        assertTrue(customerRepository.existsById(customer3.getId()));
        assertFalse(customerRepository.existsById(999L));
    }

    @Test
    @DisplayName("Should find customers by name containing substring - case insensitive")
    void shouldFindCustomersByNameContainingSubstringCaseInsensitive() {
        // When
        List<Customer> johnCustomers = customerRepository.findByNameContainingIgnoreCase("john");
        List<Customer> janeCustomers = customerRepository.findByNameContainingIgnoreCase("jane");
        List<Customer> bobCustomers = customerRepository.findByNameContainingIgnoreCase("bob");
        List<Customer> nonExistentCustomers = customerRepository.findByNameContainingIgnoreCase("nonexistent");

        // Then
        assertEquals(2, johnCustomers.size()); // "John Doe" and "Bob Johnson"
        assertTrue(johnCustomers.stream().anyMatch(c -> "John Doe".equals(c.getName())));
        assertTrue(johnCustomers.stream().anyMatch(c -> "Bob Johnson".equals(c.getName())));

        assertEquals(1, janeCustomers.size()); // "Jane Smith"
        assertEquals("Jane Smith", janeCustomers.get(0).getName());

        assertEquals(1, bobCustomers.size()); // "Bob Johnson"
        assertEquals("Bob Johnson", bobCustomers.get(0).getName());

        assertTrue(nonExistentCustomers.isEmpty());
    }

    @Test
    @DisplayName("Should find customers by name containing substring - partial match")
    void shouldFindCustomersByNameContainingSubstringPartialMatch() {
        // When
        List<Customer> doeCustomers = customerRepository.findByNameContainingIgnoreCase("doe");
        List<Customer> smithCustomers = customerRepository.findByNameContainingIgnoreCase("smith");
        List<Customer> johnsonCustomers = customerRepository.findByNameContainingIgnoreCase("johnson");

        // Then
        assertEquals(1, doeCustomers.size());
        assertEquals("John Doe", doeCustomers.get(0).getName());

        assertEquals(1, smithCustomers.size());
        assertEquals("Jane Smith", smithCustomers.get(0).getName());

        assertEquals(1, johnsonCustomers.size());
        assertEquals("Bob Johnson", johnsonCustomers.get(0).getName());
    }

    @Test
    @DisplayName("Should find customers by name containing substring - case variations")
    void shouldFindCustomersByNameContainingSubstringCaseVariations() {
        // When
        List<Customer> upperCaseCustomers = customerRepository.findByNameContainingIgnoreCase("JOHN");
        List<Customer> lowerCaseCustomers = customerRepository.findByNameContainingIgnoreCase("john");
        List<Customer> mixedCaseCustomers = customerRepository.findByNameContainingIgnoreCase("JoHn");

        // Then
        assertEquals(2, upperCaseCustomers.size());
        assertEquals(2, lowerCaseCustomers.size());
        assertEquals(2, mixedCaseCustomers.size());

        // All should return the same customers
        assertTrue(upperCaseCustomers.stream().anyMatch(c -> "John Doe".equals(c.getName())));
        assertTrue(upperCaseCustomers.stream().anyMatch(c -> "Bob Johnson".equals(c.getName())));
    }

    @Test
    @DisplayName("Should handle empty search query gracefully")
    void shouldHandleEmptySearchQueryGracefully() {
        // When
        List<Customer> emptyQueryCustomers = customerRepository.findByNameContainingIgnoreCase("");
        List<Customer> whitespaceQueryCustomers = customerRepository.findByNameContainingIgnoreCase("   ");

        // Then
        // In H2, LIKE '%%' matches all records, so empty string returns all customers
        assertEquals(3, emptyQueryCustomers.size()); // Should return all customers
        assertEquals(0, whitespaceQueryCustomers.size()); // Whitespace should return 0 customers
    }

    @Test
    @DisplayName("Should handle special characters in names")
    void shouldHandleSpecialCharactersInNames() {
        // Given
        Customer customerWithHyphen = new Customer();
        customerWithHyphen.setName("Mary-Jane Watson");
        entityManager.persistAndFlush(customerWithHyphen);

        Customer customerWithApostrophe = new Customer();
        customerWithApostrophe.setName("O'Connor");
        entityManager.persistAndFlush(customerWithApostrophe);

        entityManager.clear();

        // When
        List<Customer> hyphenCustomers = customerRepository.findByNameContainingIgnoreCase("mary");
        List<Customer> apostropheCustomers = customerRepository.findByNameContainingIgnoreCase("o'connor");

        // Then
        assertEquals(1, hyphenCustomers.size());
        assertEquals("Mary-Jane Watson", hyphenCustomers.get(0).getName());

        assertEquals(1, apostropheCustomers.size());
        assertEquals("O'Connor", apostropheCustomers.get(0).getName());
    }

    @Test
    @DisplayName("Should count total customers")
    void shouldCountTotalCustomers() {
        // When
        long count = customerRepository.count();

        // Then
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Should delete all customers")
    void shouldDeleteAllCustomers() {
        // When
        customerRepository.deleteAll();

        // Then
        assertEquals(0, customerRepository.count());
        assertTrue(customerRepository.findAll().isEmpty());
    }
}
