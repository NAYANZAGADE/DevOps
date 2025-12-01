#!/bin/bash

# Configuration
CLUSTER_NAME="ml-app-cluster"
AWS_REGION="us-east-1"
NODE_GROUP_NAME="ml-app-nodes"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🚀 Deploying ML App to EKS${NC}"

# Check if kubectl is configured for the cluster
echo -e "${YELLOW}🔧 Configuring kubectl...${NC}"
aws eks update-kubeconfig --region $AWS_REGION --name $CLUSTER_NAME

# Apply RBAC configuration
echo -e "${YELLOW}👤 Applying RBAC configuration...${NC}"
kubectl apply -f k8s-rbac.yaml

# Deploy the application
echo -e "${YELLOW}🚀 Deploying application...${NC}"
kubectl apply -f k8s-deployment.yaml

# Apply HPA
echo -e "${YELLOW}📈 Setting up auto-scaling...${NC}"
kubectl apply -f k8s-hpa.yaml

# Apply ingress (optional)
read -p "Do you want to deploy ingress? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}🌐 Deploying ingress...${NC}"
    kubectl apply -f k8s-ingress.yaml
fi

# Wait for deployment to be ready
echo -e "${YELLOW}⏳ Waiting for deployment to be ready...${NC}"
kubectl rollout status deployment/ml-app-bedrock --timeout=300s

# Get service information
echo -e "${GREEN}✅ Deployment completed!${NC}"
echo -e "${GREEN}📍 Service information:${NC}"
kubectl get services ml-app-service

# Get pod status
echo -e "${GREEN}🏃 Pod status:${NC}"
kubectl get pods -l app=ml-app-bedrock

# Get external IP (if LoadBalancer)
echo -e "${YELLOW}⏳ Waiting for external IP...${NC}"
kubectl get service ml-app-service --watch --timeout=300s

echo -e "${GREEN}🎉 Deployment to EKS completed!${NC}"