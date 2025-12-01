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

  environment   = "staging"
  app_name      = "myapp"
  instance_type = "t3.small"
  vpc_id        = data.aws_vpc.default.id
  subnet_id     = data.aws_subnets.default.ids[0]
}

output "staging_instance_ip" {
  value = module.web_server.public_ip
}
