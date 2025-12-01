#!/bin/bash

# Configuration
AWS_ACCOUNT_ID="YOUR_ACCOUNT_ID"
AWS_REGION="us-east-1"
ECR_REPOSITORY="ml-app-bedrock"
IMAGE_TAG="latest"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🚀 Building and pushing ML App to ECR${NC}"

# Get AWS account ID if not set
if [ "$AWS_ACCOUNT_ID" = "YOUR_ACCOUNT_ID" ]; then
    AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
    echo -e "${GREEN}✅ Detected AWS Account ID: $AWS_ACCOUNT_ID${NC}"
fi

# Create ECR repository if it doesn't exist
echo -e "${YELLOW}📦 Creating ECR repository if needed...${NC}"
aws ecr describe-repositories --repository-names $ECR_REPOSITORY --region $AWS_REGION 2>/dev/null || \
aws ecr create-repository --repository-name $ECR_REPOSITORY --region $AWS_REGION

# Get ECR login token
echo -e "${YELLOW}🔐 Logging into ECR...${NC}"
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# Build Docker image
echo -e "${YELLOW}🔨 Building Docker image...${NC}"
docker build -t $ECR_REPOSITORY:$IMAGE_TAG .

# Tag image for ECR
docker tag $ECR_REPOSITORY:$IMAGE_TAG $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG

# Push image to ECR
echo -e "${YELLOW}📤 Pushing image to ECR...${NC}"
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG

echo -e "${GREEN}✅ Image pushed successfully!${NC}"
echo -e "${GREEN}📍 Image URI: $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG${NC}"

# Update deployment file with correct image URI
sed -i "s|your-account\.dkr\.ecr\.us-east-1\.amazonaws\.com|$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com|g" k8s-deployment.yaml

echo -e "${GREEN}🎉 Build and push completed!${NC}"