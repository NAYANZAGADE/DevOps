variable "region" {
  description = "AWS region"
  type        = string
  default     = "ap-south-1"
}

variable "env" {
  description = "Environment name"
  type        = string
  default     = "dev"
}

variable "cidr_block" {
  description = "VPC CIDR block"
  type        = string
  default     = "10.0.0.0/16"
}

variable "instance_type" {
  description = "EC2 instance type for ECS"
  type        = string
  default     = "c7i-flex.large"
}

variable "keyname" {
  description = "EC2 key pair name"
  type        = string
  default     = "ecs-key"
}