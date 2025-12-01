# Application Load Balancer
resource "aws_lb" "alb" {
  name               = "application-load-balancer"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [var.alb_sg_id]
  subnets            =  var.public_subnet_ids

  enable_deletion_protection = false
}