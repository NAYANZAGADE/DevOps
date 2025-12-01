resource "aws_eip" "eip" {
  domain   = "vpc"
}

resource "aws_nat_gateway" "nat" {
  allocation_id = aws_eip.eip.id
  subnet_id     = aws_subnet.public1.id
  
  tags = {
    Name = "${local.env}-NAT"
  }
  depends_on = [aws_internet_gateway.igw]
}