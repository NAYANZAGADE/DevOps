resource "aws_route_table" "route_private" {
  vpc_id = aws_vpc.eks_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat.id
  }

  tags = {
    Name = "${local.env}-private"
  }
}

resource "aws_route_table" "route_public" {
  vpc_id = aws_vpc.eks_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "${local.env}-public"
  }
}

resource "aws_route_table_association" "public_az1" {
  subnet_id      = aws_subnet.public1.id
  route_table_id = aws_route_table.route_public.id
}

resource "aws_route_table_association" "public_az2" {
  subnet_id      = aws_subnet.public2.id
  route_table_id = aws_route_table.route_public.id
}
                          
resource "aws_route_table_association" "private_az1" {
  subnet_id      = aws_subnet.private1.id
  route_table_id = aws_route_table.route_private.id
}
resource "aws_route_table_association" "private_az2" {
  subnet_id      = aws_subnet.private2.id
  route_table_id = aws_route_table.route_private.id
}