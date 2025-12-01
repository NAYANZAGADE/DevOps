resource "aws_vpc" "ecs_vpc" {
  cidr_block = "${var.cidr_block}"
    tags = {
    Name = "${var.env}-glidingpath-vpc"
  }
}

#internet gateway 

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.ecs_vpc.id

  tags = {
    Name = "${var.env}-igw"
  }
}

#subnets 

resource "aws_subnet" "public_1" {
  vpc_id     = aws_vpc.ecs_vpc.id
  cidr_block = "10.0.32.0/19"
  availability_zone = var.availability_zone_1

  tags = {
    Name= "${var.env}-public_subnet_1"
    "kubernetes.io/role/elb" = "1"                  

   }
  }

resource "aws_subnet" "private_1" {
  vpc_id     = aws_vpc.ecs_vpc.id
  cidr_block = "10.0.64.0/19"
  availability_zone = var.availability_zone_1

  tags = {
    Name = "${var.env}-private_subnet_1"
    "kubernetes.io/role/internal-elb" = "1"
  }
}


resource "aws_subnet" "public_2" {
  vpc_id     = aws_vpc.ecs_vpc.id
  cidr_block = "10.0.96.0/19"
  availability_zone = var.availability_zone_2

  tags = {
    Name= "${var.env}-public_subnet_2"
    "kubernetes.io/role/elb" = "1"                  

   }
  }



resource "aws_subnet" "private_2" {
  vpc_id     = aws_vpc.ecs_vpc.id
  cidr_block = "10.0.128.0/19"
  availability_zone = var.availability_zone_2

  tags = {
    Name = "${var.env}-private_subnet_2"
    "kubernetes.io/role/internal-elb" = "1"
  }
}

#eip

resource "aws_eip" "eip" {
  domain   = "vpc"
}

#nat gateway

resource "aws_nat_gateway" "nat" {
  allocation_id = aws_eip.eip.id
  subnet_id     = aws_subnet.public_1.id

  depends_on = [aws_internet_gateway.igw]

  tags = {
    Name = "${var.env}-NAT"
  }

}


#route tables 

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.ecs_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "${var.env}-public"
  }
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.ecs_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat.id
  }

  tags = {
    Name = "${var.env}-private"
  }
}

#route table associations

resource "aws_route_table_association" "public_1" {
  subnet_id      = aws_subnet.public_1.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "public_2" {
  subnet_id      = aws_subnet.public_2.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "private_1" {
  subnet_id      = aws_subnet.private_1.id
  route_table_id = aws_route_table.private.id
}

resource "aws_route_table_association" "private_2" {
  subnet_id      = aws_subnet.private_2.id
  route_table_id = aws_route_table.private.id
}