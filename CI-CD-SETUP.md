# CI/CD Pipeline Setup Guide

This document provides comprehensive instructions for setting up the CI/CD pipeline for the Store application using GitHub Actions.

## 🚀 Overview

The CI/CD pipeline includes:
- **Automated Testing** with JaCoCo code coverage reporting
- **Docker Image Building** with multi-architecture support
- **Security Scanning** with Trivy vulnerability scanner
- **Artifact Management** with GitHub Actions artifacts
- **Environment Protection** with required reviewers

## 📋 Prerequisites

### GitHub Environment Setup

1. **Create Environment**: `assement_env`
   - Go to your repository → Settings → Environments
   - Click "New environment"
   - Name: `assement_env`

2. **Configure Environment Secrets**:
   ```
   DOCKERHUB_USERNAME: your-dockerhub-username
   DOCKERHUB_PASS: your-dockerhub-password-or-token
   ```

3. **Set Protection Rules** (Optional but Recommended):
   - **Required Reviewers**: Add team members who must approve deployments
   - **Wait Timer**: Set a delay before allowing deployments (e.g., 5 minutes)
   - **Deployment Branches**: Restrict to specific branches (e.g., `main`, `develop`)

### DockerHub Account Setup

1. Create a DockerHub account at [hub.docker.com](https://hub.docker.com)
2. Generate an access token:
   - Go to Account Settings → Security → Access Tokens
   - Create a new token with "Read, Write, Delete" permissions
   - Use this token as `DOCKERHUB_PASS` in GitHub secrets

## 🔧 Pipeline Configuration

### Workflow Triggers

The pipeline runs on:
- **Push** to `main` or `develop` branches
- **Pull Requests** targeting `main` branch

### Pipeline Stages

#### 1. Test and Code Coverage (`test` job)
- Runs on: `ubuntu-latest`
- **Actions**:
  - Checkout code
  - Setup JDK 17
  - Cache Gradle dependencies
  - Run tests with JaCoCo reporting
  - Upload coverage reports to Codecov
  - Upload JaCoCo HTML and XML reports as artifacts

#### 2. Build Application (`build` job)
- Runs on: `ubuntu-latest`
- Depends on: `test` job
- **Actions**:
  - Build the Spring Boot application
  - Upload JAR artifact

#### 3. Docker Build and Push (`docker` job)
- Runs on: `ubuntu-latest`
- Depends on: `test` and `build` jobs
- Environment: `assement_env` (requires approval)
- **Actions**:
  - Setup Docker Buildx
  - Login to DockerHub
  - Build multi-architecture Docker image (linux/amd64, linux/arm64)
  - Push to DockerHub with multiple tags
  - Generate deployment summary

#### 4. Security Scan (`security-scan` job)
- Runs on: `ubuntu-latest`
- Depends on: `docker` job
- Only runs on: `main` branch pushes
- **Actions**:
  - Run Trivy vulnerability scanner
  - Upload results to GitHub Security tab

## 🐳 Docker Configuration

### Multi-Stage Build

The Dockerfile uses a multi-stage build approach:

1. **Builder Stage**: Uses `gradle:8.5-jdk17-alpine` to build the application
2. **Runtime Stage**: Uses `openjdk:17-jre-slim` for the final image

### Security Features

- **Non-root user**: Runs as `appuser` instead of root
- **Health checks**: Built-in health monitoring
- **Minimal base image**: Uses slim JRE for smaller attack surface
- **JVM optimization**: Container-aware JVM settings

### Image Tags

The pipeline creates multiple tags:
- `latest` (for main branch)
- `main` (branch name)
- `develop` (branch name)
- `main-<commit-sha>` (commit-specific)
- `develop-<commit-sha>` (commit-specific)

## 📊 Code Coverage

### JaCoCo Configuration

- **Minimum Coverage**: 70%
- **Report Formats**: HTML, XML
- **Exclusions**: Mappers, Application class
- **Integration**: Codecov for coverage tracking

### Coverage Reports

Reports are available as:
- **GitHub Artifacts**: Downloadable HTML and XML reports
- **Codecov Integration**: Online coverage tracking
- **Build Verification**: Pipeline fails if coverage < 70%

## 🔒 Security Features

### Vulnerability Scanning

- **Trivy Scanner**: Scans Docker images for vulnerabilities
- **GitHub Security Tab**: Results integrated into GitHub's security features
- **SARIF Format**: Standardized security report format

### Environment Protection

- **Required Reviewers**: Manual approval for deployments
- **Wait Timer**: Prevents immediate deployments
- **Branch Restrictions**: Control which branches can deploy

## 🚀 Local Development

### Running with Docker Compose

```bash
# Build and start all services
docker-compose up --build

# Run in background
docker-compose up -d --build

# View logs
docker-compose logs -f store-app

# Stop services
docker-compose down
```

### Services Included

- **store-app**: Spring Boot application (port 8080)
- **postgres**: PostgreSQL database (port 5432)
- **redis**: Redis cache (port 6379)

### Health Checks

- Application: `http://localhost:8080/actuator/health`
- Database: PostgreSQL health check
- Cache: Redis ping check

## 📈 Monitoring and Observability

### Actuator Endpoints

- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

### Docker Health Checks

- **Interval**: 30 seconds
- **Timeout**: 3 seconds
- **Retries**: 3 attempts
- **Start Period**: 5 seconds

## 🛠️ Troubleshooting

### Common Issues

1. **Docker Login Fails**:
   - Verify DockerHub credentials in GitHub secrets
   - Check if access token has correct permissions

2. **Coverage Below Threshold**:
   - Review JaCoCo exclusions in `build.gradle`
   - Add more tests to increase coverage

3. **Environment Protection Blocks Deployment**:
   - Ensure required reviewers approve the deployment
   - Check if wait timer has expired

4. **Security Scan Fails**:
   - Review Trivy scan results in GitHub Security tab
   - Update base images if vulnerabilities found

### Debug Commands

```bash
# Test Docker build locally
docker build -t store-app:local .

# Run security scan locally
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy:latest image store-app:local

# Check application health
curl http://localhost:8080/actuator/health
```

## 📚 Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Multi-Architecture Builds](https://docs.docker.com/buildx/working-with-buildx/)
- [JaCoCo Gradle Plugin](https://docs.gradle.org/current/userguide/jacoco_plugin.html)
- [Trivy Security Scanner](https://trivy.dev/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

## 🎯 Next Steps

1. **Set up monitoring**: Integrate with monitoring solutions (Prometheus, Grafana)
2. **Add more environments**: Create staging and production environments
3. **Implement blue-green deployments**: Add advanced deployment strategies
4. **Add performance testing**: Integrate load testing in the pipeline
5. **Set up notifications**: Configure Slack/email notifications for pipeline events
