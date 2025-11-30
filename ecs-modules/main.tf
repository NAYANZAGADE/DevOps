terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

module "networking" {
  source = "./networking"
  
  environment = var.environment
  vpc_cidr    = var.vpc_cidr
}

module "ecs_cluster" {
  source = "./ecs-cluster"
  
  cluster_name        = "${var.environment}-cluster"
  instance_type       = var.instance_type
  min_capacity        = var.min_capacity
  max_capacity        = var.max_capacity
  desired_capacity    = var.desired_capacity
  vpc_id              = module.networking.vpc_id
  private_subnet_ids  = module.networking.private_subnet_ids
}