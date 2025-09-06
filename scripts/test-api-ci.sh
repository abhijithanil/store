#!/bin/bash

# CI/CD API Testing Script for Store Application
# Simplified version for automated testing in CI/CD pipelines

set -e  # Exit on any error

# Configuration
BASE_URL="${BASE_URL:-http://localhost:8080}"
TIMEOUT="${TIMEOUT:-30}"
MAX_ATTEMPTS="${MAX_ATTEMPTS:-30}"

# Colors for output (simplified for CI)
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Logging functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[PASS]${NC} $1"
}

log_error() {
    echo -e "${RED}[FAIL]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# Test function
test_endpoint() {
    local method="$1"
    local endpoint="$2"
    local expected_status="$3"
    local data="$4"
    local description="$5"
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    
    # Prepare curl command
    local curl_cmd="curl -s -w '%{http_code}' -o /tmp/api_response.json"
    curl_cmd="$curl_cmd --max-time $TIMEOUT"
    
    if [ "$method" = "POST" ] || [ "$method" = "PUT" ]; then
        curl_cmd="$curl_cmd -H 'Content-Type: application/json'"
        if [ -n "$data" ]; then
            curl_cmd="$curl_cmd -d '$data'"
        fi
    fi
    
    curl_cmd="$curl_cmd -X $method '$BASE_URL$endpoint'"
    
    # Execute request
    local response_code
    response_code=$(eval "$curl_cmd")
    
    # Check response
    if [ "$response_code" = "$expected_status" ]; then
        log_success "$description - Status: $response_code"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        log_error "$description - Expected: $expected_status, Got: $response_code"
        FAILED_TESTS=$((FAILED_TESTS + 1))
        
        # Log response body for debugging
        if [ -f /tmp/api_response.json ]; then
            echo "Response body:"
            cat /tmp/api_response.json
            echo ""
        fi
    fi
}

# Wait for application to be ready
wait_for_app() {
    log_info "Waiting for application to be ready..."
    local attempt=1
    
    while [ $attempt -le $MAX_ATTEMPTS ]; do
        if curl -s --max-time 5 "$BASE_URL/actuator/health" > /dev/null 2>&1; then
            log_success "Application is ready!"
            return 0
        fi
        
        log_info "Attempt $attempt/$MAX_ATTEMPTS - Application not ready yet, waiting 5 seconds..."
        sleep 5
        attempt=$((attempt + 1))
    done
    
    log_error "Application failed to start within $((MAX_ATTEMPTS * 5)) seconds"
    return 1
}

# Test health endpoint
test_health_endpoint() {
    log_info "Testing health endpoint..."
    test_endpoint "GET" "/actuator/health" "200" "" "Health check endpoint"
}

# Test Customer API endpoints
test_customer_endpoints() {
    log_info "Testing Customer API endpoints..."
    
    # Test GET /customer/all (paged)
    test_endpoint "GET" "/customer/all?page=0&size=10&sortBy=id&sortOrder=asc" "200" "" "Get all customers (paged)"
    
    # Test GET /customer/search (paged)
    test_endpoint "GET" "/customer/search?q=john&page=0&size=10&sortBy=id&sortOrder=asc" "200" "" "Search customers (paged)"
    
    # Test POST /customer (create customer)
    local customer_data='{"name": "CI Test Customer"}'
    test_endpoint "POST" "/customer" "201" "$customer_data" "Create new customer"
    
    # Test GET /customer/{id} (get customer by ID)
    test_endpoint "GET" "/customer/1" "200" "" "Get customer by ID"
}

# Test Product API endpoints
test_product_endpoints() {
    log_info "Testing Product API endpoints..."
    
    # Test GET /products (non-paged)
    test_endpoint "GET" "/products" "200" "" "Get all products (non-paged)"
    
    # Test GET /products/all (paged)
    test_endpoint "GET" "/products/all?page=0&size=10&sortBy=id&sortOrder=asc" "200" "" "Get all products (paged)"
    
    # Test GET /products/search (paged)
    test_endpoint "GET" "/products/search?q=laptop&page=0&size=10&sortBy=id&sortOrder=asc" "200" "" "Search products (paged)"
    
    # Test POST /products (create product)
    local product_data='{"description": "CI Test Product"}'
    test_endpoint "POST" "/products" "201" "$product_data" "Create new product"
    
    # Test GET /products/{id} (get product by ID)
    test_endpoint "GET" "/products/1" "200" "" "Get product by ID"
    
    # Test PUT /products/{id} (update product)
    local update_data='{"description": "Updated CI Test Product"}'
    test_endpoint "PUT" "/products/1" "200" "$update_data" "Update product"
}

# Test Order API endpoints
test_order_endpoints() {
    log_info "Testing Order API endpoints..."
    
    # Test GET /order/all (paged)
    test_endpoint "GET" "/order/all?page=0&size=10&sortBy=id&sortOrder=asc" "200" "" "Get all orders (paged)"
    
    # Test POST /order (create order)
    local order_data='{"description": "CI Test Order", "customerId": 1, "productIds": [1]}'
    test_endpoint "POST" "/order" "201" "$order_data" "Create new order"
    
    # Test GET /order/{id} (get order by ID)
    test_endpoint "GET" "/order/1" "200" "" "Get order by ID"
}

# Test Swagger/OpenAPI endpoints
test_swagger_endpoints() {
    log_info "Testing Swagger/OpenAPI endpoints..."
    
    # Test Swagger UI
    test_endpoint "GET" "/swagger-ui.html" "200" "" "Swagger UI endpoint"
    
    # Test API docs
    test_endpoint "GET" "/api-docs" "200" "" "OpenAPI documentation"
}

# Test error handling
test_error_handling() {
    log_info "Testing error handling..."
    
    # Test invalid endpoint
    test_endpoint "GET" "/invalid-endpoint" "404" "" "Invalid endpoint"
    
    # Test invalid method
    test_endpoint "DELETE" "/customer/1" "405" "" "Method not allowed"
}

# Main execution
main() {
    log_info "Starting CI/CD API tests for Store Application"
    log_info "Base URL: $BASE_URL"
    log_info "Timeout: ${TIMEOUT}s"
    echo ""
    
    # Wait for application to be ready
    if ! wait_for_app; then
        log_error "Application is not ready. Exiting."
        exit 1
    fi
    
    echo ""
    
    # Run all test suites
    test_health_endpoint
    echo ""
    
    test_customer_endpoints
    echo ""
    
    test_product_endpoints
    echo ""
    
    test_order_endpoints
    echo ""
    
    test_swagger_endpoints
    echo ""
    
    test_error_handling
    echo ""
    
    # Summary
    log_info "Test Summary:"
    log_info "Total Tests: $TOTAL_TESTS"
    log_success "Passed: $PASSED_TESTS"
    
    if [ $FAILED_TESTS -gt 0 ]; then
        log_error "Failed: $FAILED_TESTS"
        echo ""
        log_error "Some tests failed. Check the output above for details."
        exit 1
    else
        log_success "All tests passed!"
        exit 0
    fi
}

# Run main function
main
