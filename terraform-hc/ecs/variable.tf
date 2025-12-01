variable "region" {
  description = "AWS region"
  type        = string
}

variable "env" {
  description = "Environment name"
  type        = string
}
  
variable "instance_type" {
  description = "EC2 instance type"
  type        = string
}

variable "keyname" {
  description = "EC2 key pair name"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID from networking module"
  type        = string
}

variable "private_subnet_ids" {
  description = "Private subnet IDs from networking module"
  type        = list(string)
}

variable "public_subnet_ids" {
  description = "Public subnet IDs from networking module"
  type        = list(string)
}

variable "vpc_cidr" {
  description = "VPC CIDR block"
  type        = string
}

variable "ecs_instance_profile_name" {
  description = "ECS instance profile name from IAM module"
  type        = string
}

variable "ecs_sg_id" {
  description = "ECS security group ID"
  type        = string
}

