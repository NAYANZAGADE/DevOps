#!/bin/bash

# Configuration
CLUSTER_NAME="ml-app-cluster"
AWS_REGION="us-east-1"
NODE_GROUP_NAME="ml-app-nodes"
KUBERNETES_VERSION="1.28"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🏗️  Setting up EKS cluster for ML App${NC}"

# Check if eksctl is installed
if ! command -v eksctl &> /dev/null; then
    echo -e "${RED}❌ eksctl is not installed. Please install it first.${NC}"
    echo -e "${YELLOW}💡 Install with: curl --silent --location \"https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_\$(uname -s)_amd64.tar.gz\" | tar xz -C /tmp && sudo mv /tmp/eksctl /usr/local/bin${NC}"
    exit 1
fi

# Create EKS cluster
echo -e "${YELLOW}🚀 Creating EKS cluster...${NC}"
eksctl create cluster \
    --name $CLUSTER_NAME \
    --region $AWS_REGION \
    --version $KUBERNETES_VERSION \
    --nodegroup-name $NODE_GROUP_NAME \
    --node-type t3.medium \
    --nodes 2 \
    --nodes-min 1 \
    --nodes-max 4 \
    --managed \
    --with-oidc \
    --ssh-access \
    --ssh-public-key ~/.ssh/id_rsa.pub

# Install AWS Load Balancer Controller
echo -e "${YELLOW}⚖️  Installing AWS Load Balancer Controller...${NC}"

# Create IAM policy for ALB controller
curl -o iam_policy.json https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.7.2/docs/install/iam_policy.json

aws iam create-policy \
    --policy-name AWSLoadBalancerControllerIAMPolicy \
    --policy-document file://iam_policy.json

# Create service account
eksctl create iamserviceaccount \
  --cluster=$CLUSTER_NAME \
  --namespace=kube-system \
  --name=aws-load-balancer-controller \
  --role-name AmazonEKSLoadBalancerControllerRole \
  --attach-policy-arn=arn:aws:iam::$(aws sts get-caller-identity --query Account --output text):policy/AWSLoadBalancerControllerIAMPolicy \
  --approve

# Install ALB controller using Helm
helm repo add eks https://aws.github.io/eks-charts
helm repo update
helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=$CLUSTER_NAME \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller

# Create IAM role for Bedrock access
echo -e "${YELLOW}🔐 Creating IAM role for Bedrock access...${NC}"

# Create trust policy
cat > trust-policy.json << EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::$(aws sts get-caller-identity --query Account --output text):oidc-provider/$(aws eks describe-cluster --name $CLUSTER_NAME --query "cluster.identity.oidc.issuer" --output text | sed 's|https://||')"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "$(aws eks describe-cluster --name $CLUSTER_NAME --query "cluster.identity.oidc.issuer" --output text | sed 's|https://||'):sub": "system:serviceaccount:default:ml-app-service-account"
        }
      }
    }
  ]
}
EOF

# Create IAM role
aws iam create-role \
    --role-name ml-app-bedrock-role \
    --assume-role-policy-document file://trust-policy.json

# Attach Bedrock policy
aws iam attach-role-policy \
    --role-name ml-app-bedrock-role \
    --policy-arn arn:aws:iam::aws:policy/AmazonBedrockFullAccess

# Update RBAC file with correct role ARN
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
sed -i "s/YOUR_ACCOUNT_ID/$ACCOUNT_ID/g" k8s-rbac.yaml

# Clean up temporary files
rm -f iam_policy.json trust-policy.json

echo -e "${GREEN}✅ EKS cluster setup completed!${NC}"
echo -e "${GREEN}📍 Cluster name: $CLUSTER_NAME${NC}"
echo -e "${GREEN}📍 Region: $AWS_REGION${NC}"
echo -e "${GREEN}🎯 Next steps:${NC}"
echo -e "${GREEN}  1. Run: ./build-and-push.sh${NC}"
echo -e "${GREEN}  2. Run: ./deploy-to-eks.sh${NC}"