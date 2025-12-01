variable "region" {
    description = "AWS REGION"
    type = string
}

variable "public_subnet_ids" {
    description = "PUBLIC SUBNETS FOR ALB"
    type = list(string)  
}

variable "alb_sg_id" {
  description = "ALB security group ID"
  type        = string
}