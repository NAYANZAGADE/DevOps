terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-west-2"
}

# Use existing default VPC and subnet for simplicity
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

module "web_server" {
  source = "../../modules/web-server"

  environment   = "dev"
  app_name      = "myapp"
  instance_type = "t3.micro"
  vpc_id        = data.aws_vpc.default.id
  subnet_id     = data.aws_subnets.default.ids[0]
}

output "dev_instance_ip" {
  value = module.web_server.public_ip
}
