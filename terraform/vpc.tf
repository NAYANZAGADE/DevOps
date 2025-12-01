resource "aws_vpc" "eks_vpc" {
  cidr_block       = "10.0.0.0/16"
  instance_tenancy = "default"
  
  enable_dns_support = true  # allows resources in your VPC to resolve DNS queries (without this you cannot connect or ping or curl to domain names eg.(google.com))
  enable_dns_hostnames = true # automatically assign public DNS hostnames to EC2 instances that have public IPs eg: ec2-13-232-X-X.ap-south-1.compute.amazonaws.com

  tags = {
    Name = "${local.VPC_Name}-eks-vpc"
  }
}