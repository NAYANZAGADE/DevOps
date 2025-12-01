terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "6.22.1"
    }
  }

  backend "local" {
    path = "dev/vpc/terraform.tfstate"
  }
}

provider "aws" {
  region = "ap-south-1"
}