resource "aws_subnet" "public1" {
  vpc_id     = aws_vpc.eks_vpc.id
  cidr_block = "10.0.0.0/20"
  availability_zone = "${local.AZ1}"
  map_public_ip_on_launch = true
  tags = {
    "Name"= "${local.VPC_Name}-public_subnet-${local.AZ1}"
    "kubernetes.io/cluster/${local.eks_name}" = "shared"
    "kubernetes.io/role/elb" = "1"                  

  }
}

resource "aws_subnet" "public2" {
  vpc_id     = aws_vpc.eks_vpc.id
  cidr_block = "10.0.16.0/20"
  availability_zone = "${local.AZ2}"
  map_public_ip_on_launch = true
  tags = {
    "Name"= "${local.VPC_Name}-public_subnet-${local.AZ2}"
    "kubernetes.io/cluster/${local.eks_name}" = "shared"
    "kubernetes.io/role/elb" = "1"                  

  }
}


resource "aws_subnet" "private1" {
  vpc_id     = aws_vpc.eks_vpc.id
  cidr_block = "10.0.32.0/19"
  availability_zone = "${local.AZ1}"
  
  tags = {
   "Name"= "${local.VPC_Name}-private_subnet-${local.AZ1}"
    "kubernetes.io/role/internal-elb"    =      "1"
    "kubernetes.io/cluster/${local.eks_name}" =   "shared"

  }
}

resource "aws_subnet" "private2" {
  vpc_id     = aws_vpc.eks_vpc.id
  cidr_block = "10.0.64.0/19"
  availability_zone = "${local.AZ2}"
  
  tags = {
   "Name"= "${local.VPC_Name}-private_subnet-${local.AZ2}"
    "kubernetes.io/role/internal-elb"         =      "1"
    "kubernetes.io/cluster/${local.eks_name}" =   "shared"

  }
}