# EKS Deployment Guide for ML Application

This guide walks you through deploying your ML application on Amazon EKS.

## Prerequisites

1. **AWS CLI** configured with appropriate permissions
2. **Docker** installed and running
3. **kubectl** installed
4. **eksctl** installed
5. **Helm** installed (for ALB controller)

## Quick Start

### 1. Setup EKS Cluster
```bash
chmod +x setup-eks-cluster.sh
./setup-eks-cluster.sh
```

### 2. Build and Push Docker Image
```bash
chmod +x build-and-push.sh
./build-and-push.sh
```

### 3. Deploy to EKS
```bash
chmod +x deploy-to-eks.sh
./deploy-to-eks.sh
```

## Manual Deployment Steps

### Step 1: Create EKS Cluster
```bash
eksctl create cluster \
    --name ml-app-cluster \
    --region us-east-1 \
    --version 1.28 \
    --nodegroup-name ml-app-nodes \
    --node-type t3.medium \
    --nodes 2 \
    --nodes-min 1 \
    --nodes-max 4 \
    --managed \
    --with-oidc
```

### Step 2: Build and Push Docker Image
```bash
# Get AWS account ID
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

# Create ECR repository
aws ecr create-repository --repository-name ml-app-bedrock --region us-east-1

# Build and tag image
docker build -t ml-app-bedrock:latest .
docker tag ml-app-bedrock:latest $AWS_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/ml-app-bedrock:latest

# Login to ECR and push
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com
docker push $AWS_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/ml-app-bedrock:latest
```

### Step 3: Deploy Application
```bash
# Apply RBAC
kubectl apply -f k8s-rbac.yaml

# Deploy application
kubectl apply -f k8s-deployment.yaml

# Setup auto-scaling
kubectl apply -f k8s-hpa.yaml

# Optional: Deploy ingress
kubectl apply -f k8s-ingress.yaml
```

## Configuration Files

### Kubernetes Manifests
- `k8s-deployment.yaml` - Main deployment and service
- `k8s-rbac.yaml` - Service account and permissions
- `k8s-ingress.yaml` - ALB ingress controller
- `k8s-hpa.yaml` - Horizontal pod autoscaler
- `k8s-monitoring.yaml` - Prometheus monitoring

### Scripts
- `setup-eks-cluster.sh` - Complete EKS cluster setup
- `build-and-push.sh` - Docker build and ECR push
- `deploy-to-eks.sh` - Deploy to existing EKS cluster

## Monitoring and Scaling

### Auto-scaling
The HPA is configured to scale based on:
- CPU utilization (70% threshold)
- Memory utilization (80% threshold)
- Min replicas: 2
- Max replicas: 10

### Monitoring
Deploy Prometheus for monitoring:
```bash
kubectl apply -f k8s-monitoring.yaml
```

## Accessing the Application

### Via LoadBalancer
```bash
kubectl get service ml-app-service
# Use the EXTERNAL-IP to access the app
```

### Via Ingress (if configured)
Update `k8s-ingress.yaml` with your domain and deploy:
```bash
kubectl apply -f k8s-ingress.yaml
```

## Troubleshooting

### Check Pod Status
```bash
kubectl get pods -l app=ml-app-bedrock
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

### Check Service
```bash
kubectl get services
kubectl describe service ml-app-service
```

### Check Ingress
```bash
kubectl get ingress
kubectl describe ingress ml-app-ingress
```

### Common Issues

1. **Image Pull Errors**: Ensure ECR permissions and correct image URI
2. **Bedrock Access Denied**: Check IAM role and service account annotations
3. **LoadBalancer Pending**: Check AWS Load Balancer Controller installation
4. **Pod Crashes**: Check resource limits and AWS credentials

## Security Considerations

1. **IAM Roles**: Uses IRSA (IAM Roles for Service Accounts)
2. **Network Policies**: Consider implementing network policies
3. **Secrets Management**: Use AWS Secrets Manager or Kubernetes secrets
4. **Image Scanning**: Enable ECR image scanning

## Cost Optimization

1. **Node Types**: Adjust instance types based on workload
2. **Spot Instances**: Consider using spot instances for cost savings
3. **Auto-scaling**: Properly configure HPA to avoid over-provisioning
4. **Resource Requests**: Set appropriate resource requests and limits

## Cleanup

To delete everything:
```bash
kubectl delete -f k8s-deployment.yaml
kubectl delete -f k8s-rbac.yaml
kubectl delete -f k8s-hpa.yaml
kubectl delete -f k8s-ingress.yaml
eksctl delete cluster --name ml-app-cluster --region us-east-1
```

## Next Steps

1. Set up CI/CD pipeline
2. Implement blue-green deployments
3. Add more comprehensive monitoring
4. Set up log aggregation
5. Implement backup strategies