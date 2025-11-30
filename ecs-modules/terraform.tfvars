environment = "dev"
aws_region  = "ap-south-1"
vpc_cidr    = "10.0.0.0/16"

# ECS Configuration
instance_type    = "t3.small"
min_capacity     = 1
max_capacity     = 3
desired_capacity = 2