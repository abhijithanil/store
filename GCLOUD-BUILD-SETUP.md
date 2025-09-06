# Google Cloud Build Setup Guide

This guide will help you set up Google Cloud Build for the Spring Boot Store Application.

## Prerequisites

1. **Google Cloud Account** - You need a Google Cloud account with billing enabled
2. **Google Cloud SDK** - Install the Google Cloud SDK on your machine
3. **Project** - Create or select a Google Cloud project

## Quick Setup

### 1. Install Google Cloud SDK

```bash
# macOS
brew install google-cloud-sdk

# Windows (using Chocolatey)
choco install gcloudsdk

# Linux
curl https://sdk.cloud.google.com | bash
exec -l $SHELL
```

### 2. Authenticate and Set Project

```bash
# Login to Google Cloud
gcloud auth login

# Set your project ID
gcloud config set project YOUR_PROJECT_ID

# Verify the setup
gcloud config list
```

### 3. Run Setup Script

```bash
# Make the script executable (if not already)
chmod +x setup-gcloud-build.sh

# Run the setup script
./setup-gcloud-build.sh
```

## Manual Setup

If you prefer to set up manually, follow these steps:

### 1. Enable Required APIs

```bash
gcloud services enable cloudbuild.googleapis.com
gcloud services enable containerregistry.googleapis.com
gcloud services enable run.googleapis.com
```

### 2. Create Storage Bucket

```bash
# Create bucket for build artifacts
gsutil mb gs://YOUR_PROJECT_ID-build-artifacts
```

### 3. Set Up Permissions

```bash
# Grant Cloud Build service account necessary permissions
gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
    --member="serviceAccount:YOUR_PROJECT_ID@cloudbuild.gserviceaccount.com" \
    --role="roles/storage.admin"

gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
    --member="serviceAccount:YOUR_PROJECT_ID@cloudbuild.gserviceaccount.com" \
    --role="roles/container.developer"
```

## Build Configuration

### Cloud Build Steps

The `cloudbuild.yaml` file defines the following build steps:

1. **Test** - Run unit tests and generate coverage report
2. **Build** - Compile the application
3. **Docker Build** - Create Docker image
4. **Push** - Push image to Google Container Registry
5. **API Tests** - Run integration tests against the built application

### Build Triggers

#### GitHub Integration

To connect your GitHub repository:

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Navigate to Cloud Build > Triggers
3. Click "Create Trigger"
4. Connect your GitHub repository
5. Configure the trigger:
   - **Name**: `store-app-ci-cd`
   - **Event**: Push to a branch
   - **Branch**: `main`
   - **Configuration**: Cloud Build configuration file
   - **Location**: `cloudbuild.yaml`

#### Manual Build

```bash
# Run a manual build
gcloud builds submit --config=cloudbuild.yaml .

# Run with specific substitutions
gcloud builds submit --config=cloudbuild.yaml \
  --substitutions=_IMAGE_NAME=store-app,_REGION=us-central1 .
```

## Build Features

### ✅ What's Included

- **Automated Testing** - Unit tests with JaCoCo coverage
- **Docker Build** - Multi-stage Docker build
- **Image Registry** - Push to Google Container Registry
- **API Testing** - Integration tests against running application
- **Artifact Storage** - Build artifacts stored in Cloud Storage
- **Logging** - Comprehensive build logs

### 🎯 Build Triggers

- **Push to main branch** - Automatic builds on code changes
- **Pull requests** - Build validation for PRs
- **Manual trigger** - On-demand builds
- **Scheduled builds** - Regular builds (optional)

### 📊 Build Artifacts

- **Application JAR** - Built Spring Boot application
- **Docker Image** - Containerized application
- **Test Reports** - JaCoCo coverage reports
- **API Test Results** - Integration test results

## Monitoring and Debugging

### View Build Logs

```bash
# List recent builds
gcloud builds list

# View specific build logs
gcloud builds log BUILD_ID

# Stream logs for running build
gcloud builds log --stream BUILD_ID
```

### Common Issues

1. **Permission Errors**
   ```bash
   # Ensure service account has proper permissions
   gcloud projects get-iam-policy YOUR_PROJECT_ID
   ```

2. **API Not Enabled**
   ```bash
   # Enable required APIs
   gcloud services enable cloudbuild.googleapis.com
   ```

3. **Storage Issues**
   ```bash
   # Check bucket permissions
   gsutil iam get gs://YOUR_PROJECT_ID-build-artifacts
   ```

## Cost Optimization

### Build Optimization

- **Machine Type**: Uses `E2_HIGHCPU_8` for faster builds
- **Caching**: Gradle dependencies cached between builds
- **Parallel Steps**: Multiple build steps run in parallel where possible

### Storage Optimization

- **Artifact Retention**: Configure retention policies for build artifacts
- **Cleanup**: Regular cleanup of old Docker images

## Security

### Service Account

- Uses Cloud Build default service account
- Minimal required permissions
- No long-lived credentials stored

### Container Registry

- Images stored in Google Container Registry
- Access controlled via IAM
- Vulnerability scanning available

## Next Steps

1. **Connect Repository** - Link your GitHub repository
2. **Create Trigger** - Set up automatic builds
3. **Test Build** - Run your first build
4. **Monitor** - Set up build notifications
5. **Deploy** - Configure deployment to Cloud Run or GKE

## Support

- **Google Cloud Build Documentation**: https://cloud.google.com/build/docs
- **Cloud Build Samples**: https://github.com/GoogleCloudPlatform/cloud-build-samples
- **Community Support**: https://cloud.google.com/support

---

🎉 **Happy Building with Google Cloud Build!**
