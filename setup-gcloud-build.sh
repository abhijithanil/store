#!/bin/bash

# Google Cloud Build Setup Script for Spring Boot Store Application
# This script sets up Google Cloud Build for the project

set -e

echo "🚀 Setting up Google Cloud Build for Store Application..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if gcloud is installed
if ! command -v gcloud &> /dev/null; then
    print_error "Google Cloud SDK is not installed. Please install it first:"
    echo "https://cloud.google.com/sdk/docs/install"
    exit 1
fi

print_success "Google Cloud SDK is installed"

# Get project ID
PROJECT_ID=$(gcloud config get-value project 2>/dev/null)
if [ -z "$PROJECT_ID" ]; then
    print_error "No project ID set. Please set it using:"
    echo "gcloud config set project YOUR_PROJECT_ID"
    exit 1
fi

print_status "Using project: $PROJECT_ID"

# Enable required APIs
print_status "Enabling required Google Cloud APIs..."
gcloud services enable cloudbuild.googleapis.com
gcloud services enable containerregistry.googleapis.com
gcloud services enable run.googleapis.com

print_success "APIs enabled successfully"

# Create Cloud Storage bucket for build artifacts
BUCKET_NAME="${PROJECT_ID}-build-artifacts"
print_status "Creating Cloud Storage bucket for build artifacts: $BUCKET_NAME"

if gsutil ls -b gs://$BUCKET_NAME &>/dev/null; then
    print_warning "Bucket $BUCKET_NAME already exists"
else
    gsutil mb gs://$BUCKET_NAME
    print_success "Bucket $BUCKET_NAME created successfully"
fi

# Set up Cloud Build service account permissions
print_status "Setting up Cloud Build service account permissions..."
gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${PROJECT_ID}@cloudbuild.gserviceaccount.com" \
    --role="roles/storage.admin"

gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${PROJECT_ID}@cloudbuild.gserviceaccount.com" \
    --role="roles/container.developer"

print_success "Service account permissions configured"

# Create build trigger (if repository is connected)
print_status "To create a build trigger, you need to:"
echo "1. Connect your repository to Google Cloud Build"
echo "2. Run the following command:"
echo ""
echo "gcloud builds triggers create github \\"
echo "  --repo-name=store \\"
echo "  --repo-owner=YOUR_GITHUB_USERNAME \\"
echo "  --branch-pattern=main \\"
echo "  --build-config=cloudbuild.yaml \\"
echo "  --description='CI/CD pipeline for Store Application'"
echo ""

# Test the build configuration
print_status "Testing build configuration..."
if gcloud builds submit --config=cloudbuild.yaml --dry-run .; then
    print_success "Build configuration is valid"
else
    print_error "Build configuration has issues. Please check cloudbuild.yaml"
    exit 1
fi

print_success "Google Cloud Build setup completed!"
echo ""
echo "📋 Next steps:"
echo "1. Connect your GitHub repository to Google Cloud Build"
echo "2. Create a build trigger using the command above"
echo "3. Push your code to trigger the first build"
echo ""
echo "🔧 Manual build command:"
echo "gcloud builds submit --config=cloudbuild.yaml ."
echo ""
echo "📊 View builds:"
echo "gcloud builds list"
echo ""
echo "🎉 Happy building!"
