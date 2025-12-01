# ECS with EC2 - Keycloak and Redis Example
# Main configuration file - resources are organized in separate files:
# - providers.tf: Terraform and AWS provider configuration
# - variables.tf: Input variables
# - networking.tf: VPC, subnets, routing
# - security.tf: Security groups
# - iam.tf: IAM roles and policies
# - ecs.tf: ECS cluster and EC2 instances
# - load_balancer.tf: Application Load Balancer
# - applications.tf: Keycloak and Redis task definitions and services
# - outputs.tf: Output values

# This file serves as the main entry point and can contain any additional
# resources or data sources that don't fit into the other categories
