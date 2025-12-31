#!/bin/bash

# Intelligent Document Processing Pipeline - Deployment Script
# This script handles the complete deployment to Kubernetes

set -e

echo "Starting Intelligent Document Processing Pipeline Deployment"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
NAMESPACE=${NAMESPACE:-default}
DOCKER_REGISTRY=${DOCKER_REGISTRY:-your-registry.com}
IMAGE_TAG=${IMAGE_TAG:-latest}

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."

    if ! command -v kubectl &> /dev/null; then
        print_error "kubectl is not installed. Please install it first."
        exit 1
    fi

    if ! command -v docker &> /dev/null; then
        print_error "docker is not installed. Please install it first."
        exit 1
    fi

    if ! kubectl cluster-info &> /dev/null; then
        print_error "Unable to connect to Kubernetes cluster."
        exit 1
    fi

    print_status "Prerequisites check passed."
}

# Build and push Docker images
build_and_push_images() {
    print_status "Building and pushing Docker images..."

    # Build backend image
    print_status "Building backend image..."
    docker build -f docker/Dockerfile.backend.prod -t ${DOCKER_REGISTRY}/intelligent-doc-backend:${IMAGE_TAG} backend/
    docker push ${DOCKER_REGISTRY}/intelligent-doc-backend:${IMAGE_TAG}

    # Build frontend image
    print_status "Building frontend image..."
    docker build -f docker/Dockerfile.frontend.prod -t ${DOCKER_REGISTRY}/intelligent-doc-frontend:${IMAGE_TAG} frontend/
    docker push ${DOCKER_REGISTRY}/intelligent-doc-frontend:${IMAGE_TAG}

    print_status "Images built and pushed successfully."
}

# Deploy to Kubernetes
deploy_to_k8s() {
    print_status "Deploying to Kubernetes..."

    # Create namespace if it doesn't exist
    kubectl create namespace ${NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -

    # Update image tags in deployment files
    sed -i "s|image: intelligent-doc-backend:latest|image: ${DOCKER_REGISTRY}/intelligent-doc-backend:${IMAGE_TAG}|g" k8s/backend-deployment.yaml
    sed -i "s|image: intelligent-doc-frontend:latest|image: ${DOCKER_REGISTRY}/intelligent-doc-frontend:${IMAGE_TAG}|g" k8s/frontend-deployment.yaml

    # Apply Kubernetes manifests
    print_status "Applying secrets and configmaps..."
    kubectl apply -f k8s/secrets.yaml -n ${NAMESPACE}
    kubectl apply -f k8s/configmap.yaml -n ${NAMESPACE}

    print_status "Deploying infrastructure..."
    kubectl apply -f k8s/postgres-deployment.yaml -n ${NAMESPACE}
    kubectl apply -f k8s/redis-deployment.yaml -n ${NAMESPACE}
    kubectl apply -f k8s/kafka-deployment.yaml -n ${NAMESPACE}
    kubectl apply -f k8s/elk-deployment.yaml -n ${NAMESPACE}

    print_status "Waiting for infrastructure to be ready..."
    kubectl wait --for=condition=available --timeout=300s deployment/postgres -n ${NAMESPACE}
    kubectl wait --for=condition=available --timeout=300s deployment/redis -n ${NAMESPACE}
    kubectl wait --for=condition=available --timeout=300s deployment/zookeeper -n ${NAMESPACE}
    kubectl wait --for=condition=available --timeout=300s deployment/kafka -n ${NAMESPACE}

    print_status "Deploying application..."
    kubectl apply -f k8s/backend-deployment.yaml -n ${NAMESPACE}
    kubectl apply -f k8s/frontend-deployment.yaml -n ${NAMESPACE}
    kubectl apply -f k8s/ingress.yaml -n ${NAMESPACE}

    print_status "Waiting for application to be ready..."
    kubectl wait --for=condition=available --timeout=300s deployment/intelligent-doc-backend -n ${NAMESPACE}
    kubectl wait --for=condition=available --timeout=300s deployment/intelligent-doc-frontend -n ${NAMESPACE}

    print_status "Deployment completed successfully!"
}

# Run health checks
run_health_checks() {
    print_status "Running health checks..."

    # Wait for ingress to be ready
    sleep 30

    # Get ingress IP/hostname
    INGRESS_HOST=$(kubectl get ingress intelligent-doc-ingress -n ${NAMESPACE} -o jsonpath='{.spec.rules[0].host}')

    if [ -z "$INGRESS_HOST" ]; then
        print_warning "Could not determine ingress host. Manual health check required."
        return
    fi

    # Check backend health
    if curl -f -k https://${INGRESS_HOST}/api/actuator/health; then
        print_status "Backend health check passed."
    else
        print_error "Backend health check failed."
        exit 1
    fi

    # Check frontend
    if curl -f -k https://${INGRESS_HOST}/; then
        print_status "Frontend health check passed."
    else
        print_error "Frontend health check failed."
        exit 1
    fi
}

# Rollback function
rollback() {
    print_warning "Starting rollback..."

    kubectl rollout undo deployment/intelligent-doc-backend -n ${NAMESPACE}
    kubectl rollout undo deployment/intelligent-doc-frontend -n ${NAMESPACE}

    print_status "Rollback completed."
}

# Main deployment flow
main() {
    echo "Intelligent Document Processing Pipeline Deployment"
    echo "=================================================="

    check_prerequisites

    # Trap for cleanup on error
    trap 'print_error "Deployment failed!"; rollback' ERR

    build_and_push_images
    deploy_to_k8s
    run_health_checks

    print_status "Deployment completed successfully!"
    print_status "Application is available at: https://${INGRESS_HOST}"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --namespace=*)
            NAMESPACE="${1#*=}"
            shift
            ;;
        --registry=*)
            DOCKER_REGISTRY="${1#*=}"
            shift
            ;;
        --tag=*)
            IMAGE_TAG="${1#*=}"
            shift
            ;;
        --help)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --namespace=NAMESPACE    Kubernetes namespace (default: default)"
            echo "  --registry=REGISTRY      Docker registry (default: your-registry.com)"
            echo "  --tag=TAG                Image tag (default: latest)"
            echo "  --help                   Show this help message"
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            exit 1
            ;;
    esac
done

main "$@"
