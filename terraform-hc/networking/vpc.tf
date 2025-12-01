resource "aws_vpc" "ecs_vpc" {
  cidr_block           = var.cidr_block
  enable_dns_hostnames = true
  enable_dns_support   = true
  
  tags = {
    Name = "${var.env}-glidingpath-vpc"
  }
}
