# ECS with EC2 - Keycloak and Redis Example

This example demonstrates how to deploy Keycloak (identity management) and Redis (caching) using Amazon ECS with EC2 instances (not Fargate).

## What This Example Includes

### Infrastructure Components:
- **VPC with Public and Private Subnets**: Network foundation across 2 availability zones
- **ECS Cluster**: Container orchestration platform
- **EC2 Auto Scaling Group**: Manages EC2 instances that run containers
- **Application Load Balancer**: Distributes traffic to Keycloak
- **RDS PostgreSQL**: Managed database for Keycloak data persistence
- **Security Groups**: Network security rules for ECS, RDS, and load balancer
- **IAM Roles**: Permissions for ECS instances and RDS monitoring

### Applications:
- **Keycloak Container**: Identity and access management server (quay.io/keycloak/keycloak:23.0)
- **Redis Container**: In-memory data store for caching (redis:7-alpine)
- **PostgreSQL Database**: Persistent storage for Keycloak configuration and user data
- **Task Definitions**: Describe how to run each container
- **ECS Services**: Ensure desired number of containers are running

## Key ECS Concepts Explained

### 1. ECS Cluster
The cluster is a logical grouping of EC2 instances where your containers run. Think of it as your container hosting environment.

### 2. Task Definition
A blueprint that describes:
- Which Docker image to use
- CPU and memory requirements
- Port mappings
- Environment variables
- Logging configuration

### 3. ECS Service
Manages running tasks:
- Ensures desired number of tasks are running
- Handles load balancer integration
- Manages task placement across instances

### 4. EC2 Launch Type vs Fargate
- **EC2 Launch Type** (this example): You manage the EC2 instances
- **Fargate**: AWS manages the infrastructure for you

## Deployment Steps

1. **Initialize Terraform**:
   ```bash
   terraform init
   ```

2. **Plan the deployment**:
   ```bash
   terraform plan
   ```

3. **Apply the configuration**:
   ```bash
   terraform apply
   ```

4. **Access your applications**:
   - Keycloak: Use the load balancer DNS name from the output
   - Admin Console: `http://<load-balancer-dns>/admin`
   - Credentials: admin / admin123

## What Happens During Deployment

1. **Infrastructure Setup**: VPC, public/private subnets, security groups, and load balancer are created
2. **Database Creation**: RDS PostgreSQL instance is launched in private subnets
3. **ECS Cluster Creation**: The cluster is established
4. **EC2 Instances Launch**: Auto Scaling Group launches ECS-optimized EC2 instances (t3.small for more memory)
5. **ECS Agent Registration**: Instances automatically register with the cluster
6. **Redis Deployment**: Redis container starts first for caching support
7. **Keycloak Deployment**: Keycloak container starts and connects to PostgreSQL database
8. **Load Balancer Integration**: ALB routes traffic to healthy Keycloak containers

## Monitoring and Troubleshooting

- **ECS Console**: View cluster, services, and tasks
- **RDS Console**: Monitor database performance and connections
- **CloudWatch Logs**: 
  - Keycloak logs: `/ecs/keycloak-app` log group
  - Redis logs: `/ecs/redis-app` log group
- **Load Balancer Health Checks**: Monitor Keycloak container health
- **Keycloak Health Check**: `/health/ready` endpoint
- **Database Monitoring**: RDS Enhanced Monitoring and Performance Insights enabled

## Cost Considerations

This example uses:
- t3.small EC2 instances (more memory for Keycloak, small hourly cost)
- db.t3.micro RDS instance (eligible for free tier, minimal cost)
- Application Load Balancer (small hourly cost)
- CloudWatch Logs (minimal cost for logs)
- RDS storage and backup (minimal cost for 20GB)

## Cleanup

To avoid charges, destroy the infrastructure when done learning:
```bash
terraform destroy
```

## Next Steps for Learning

1. **Keycloak Configuration**:
   - Create realms and users through the admin console
   - Configure authentication flows
   - Set up client applications

2. **Redis Integration**:
   - Configure Keycloak to use Redis for session storage
   - Monitor Redis performance and memory usage

3. **Scaling**:
   - Scale Keycloak service up/down by changing `desired_count`
   - Experiment with different instance types for better performance

4. **Security Enhancements**:
   - Add HTTPS/SSL termination at the load balancer
   - Use AWS Secrets Manager for Keycloak admin password
   - Restrict security group rules

5. **Database Management**:
   - Connect to PostgreSQL database for direct queries
   - Configure database backups and maintenance windows
   - Monitor database performance and optimize queries

6. **Advanced Configuration**:
   - Configure Keycloak to use Redis for session clustering
   - Set up database connection pooling
   - Implement database migration strategies