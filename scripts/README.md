# API Testing Scripts

This directory contains shell scripts for testing the Store Application API endpoints.

## Scripts Overview

### 1. `test-api.sh` - Interactive API Testing Script

**Purpose**: Comprehensive API testing with interactive features and detailed output.

**Features**:
- Interactive command-line options
- Detailed response logging
- Performance testing
- Comprehensive endpoint coverage
- Color-coded output
- Help documentation

**Usage**:
```bash
# Basic usage
./scripts/test-api.sh

# Test different environment
./scripts/test-api.sh -u http://staging.example.com

# Verbose output with custom timeout
./scripts/test-api.sh -v -t 60

# Show help
./scripts/test-api.sh -h
```

**Options**:
- `-u, --url URL`: Base URL for the API (default: http://localhost:8080)
- `-t, --timeout SEC`: Request timeout in seconds (default: 30)
- `-v, --verbose`: Show detailed response bodies
- `-h, --help`: Show help message

### 2. `test-api-ci.sh` - CI/CD API Testing Script

**Purpose**: Simplified API testing for automated CI/CD pipelines.

**Features**:
- Non-interactive execution
- Simplified output for CI logs
- Essential endpoint testing
- Error handling for CI environments
- Automatic application startup detection

**Usage**:
```bash
# Basic usage (for CI/CD)
./scripts/test-api-ci.sh

# With environment variables
BASE_URL=http://localhost:8080 TIMEOUT=30 ./scripts/test-api-ci.sh
```

**Environment Variables**:
- `BASE_URL`: Base URL for the API (default: http://localhost:8080)
- `TIMEOUT`: Request timeout in seconds (default: 30)
- `MAX_ATTEMPTS`: Maximum attempts to wait for app startup (default: 30)

## Test Coverage

Both scripts test the following API endpoints:

### Health & Monitoring
- `GET /actuator/health` - Application health check

### Customer API
- `GET /customer/all` - Get all customers (paged)
- `GET /customer/search` - Search customers (paged)
- `POST /customer` - Create new customer
- `GET /customer/{id}` - Get customer by ID

### Product API
- `GET /products` - Get all products (non-paged)
- `GET /products/all` - Get all products (paged)
- `GET /products/search` - Search products (paged)
- `POST /products` - Create new product
- `GET /products/{id}` - Get product by ID
- `PUT /products/{id}` - Update product

### Order API
- `GET /order/all` - Get all orders (paged)
- `POST /order` - Create new order
- `GET /order/{id}` - Get order by ID

### Documentation
- `GET /swagger-ui.html` - Swagger UI
- `GET /api-docs` - OpenAPI documentation

### Error Handling
- Invalid endpoints (404)
- Method not allowed (405)
- Invalid request data (400)

## Prerequisites

### Required Tools
- `curl` - For HTTP requests
- `jq` - For JSON formatting (optional, recommended)

### Application Requirements
- Store Application running on specified URL
- PostgreSQL database accessible
- Redis cache accessible (if caching is enabled)

## Local Development Testing

### Using Docker Compose
```bash
# Start the application with dependencies
docker-compose up -d

# Wait for services to be ready
sleep 30

# Run API tests
./scripts/test-api.sh

# Stop services
docker-compose down
```

### Manual Testing
```bash
# Start the application manually
java -jar build/libs/store-1.0.0-SNAPSHOT.jar

# In another terminal, run tests
./scripts/test-api.sh
```

## CI/CD Integration

The `test-api-ci.sh` script is automatically executed in the GitHub Actions CI/CD pipeline:

1. **Application Startup**: The CI pipeline starts the application with test database
2. **Health Check**: Waits for application to be ready
3. **API Testing**: Runs comprehensive API tests
4. **Results**: Uploads test results as artifacts

### CI Pipeline Features
- **Service Dependencies**: PostgreSQL and Redis services
- **Automatic Startup**: Application starts with test configuration
- **Health Monitoring**: Waits for application readiness
- **Cleanup**: Properly stops application after tests
- **Artifacts**: Uploads test results for debugging

## Troubleshooting

### Common Issues

1. **Application Not Ready**
   ```
   [ERROR] Application failed to start within 150 seconds
   ```
   - Check if application is running
   - Verify database connectivity
   - Check application logs

2. **Connection Refused**
   ```
   curl: (7) Failed to connect to localhost port 8080: Connection refused
   ```
   - Ensure application is running on correct port
   - Check firewall settings
   - Verify BASE_URL configuration

3. **Test Failures**
   ```
   [FAIL] Create new customer - Expected: 201, Got: 400
   ```
   - Check request data format
   - Verify database schema
   - Review application logs

### Debug Mode

Enable verbose output for detailed debugging:
```bash
# Interactive script
./scripts/test-api.sh -v

# CI script
VERBOSE=true ./scripts/test-api-ci.sh
```

### Manual Testing

Test individual endpoints manually:
```bash
# Health check
curl -f http://localhost:8080/actuator/health

# Create customer
curl -X POST http://localhost:8080/customer \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Customer"}'

# Get customers
curl http://localhost:8080/customer/all?page=0&size=10
```

## Customization

### Adding New Tests

To add new endpoint tests, modify the test functions in the scripts:

```bash
# Add to test_customer_endpoints() function
test_endpoint "GET" "/customer/new-endpoint" "200" "" "New customer endpoint"
```

### Environment-Specific Testing

Create environment-specific test configurations:

```bash
# Production testing
BASE_URL=https://api.production.com ./scripts/test-api.sh

# Staging testing
BASE_URL=https://api.staging.com ./scripts/test-api.sh
```

### Performance Testing

The interactive script includes basic performance testing:
- Response time measurement
- Performance thresholds
- Performance warnings

## Best Practices

1. **Always test locally** before pushing to CI
2. **Use appropriate timeouts** for different environments
3. **Verify test data** doesn't conflict with existing data
4. **Clean up test data** after testing
5. **Monitor test results** in CI/CD pipeline
6. **Update tests** when API changes

## Contributing

When adding new API endpoints:
1. Add tests to both scripts
2. Update this documentation
3. Test locally and in CI
4. Ensure backward compatibility
