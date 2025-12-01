
resource "aws_ecs_cluster" "ecs" {
  name = "${var.env}-glidingpath"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }
}

# resource "aws_ecs_cluster_capacity_providers" "ecs" {
#   cluster_name = aws_ecs_cluster.ecs.name

#   capacity_providers = ["FARGATE_SPOT", "FARGATE"]

#   default_capacity_provider_strategy {
#     base              = 1
#     weight            = 100
#     capacity_provider = "FARGATE_SPOT"
#   }
# }

data "aws_ami" "ecs_optimized" {
  most_recent = true

  filter {
    name   = "name"
    values = ["amzn2-ami-ecs-hvm-*-x86_64-ebs"]
  }

  owners = ["amazon"]
}

#iam role 

resource "aws_iam_role" "ecs_instance_role" {
  name = "${var.env}-ecs-instance-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_ec2_policy" {
  role       = aws_iam_role.ecs_instance_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ecs_instance_profile" {
  name = "${var.env}-ecs-instance-profile"
  role = aws_iam_role.ecs_instance_role.name
}


# launch template  

resource "aws_launch_template" "ecs" {
  name_prefix   = "${var.env}-ecs-lt"
  instance_type = "c7i-flex.large"
  image_id      = data.aws_ami.ecs_optimized.id
  key_name      = "ecs-key"

  iam_instance_profile {
    name = aws_iam_instance_profile.ecs_instance_profile.name
  }

  vpc_security_group_ids = [aws_security_group.ecs_sg.id]

  user_data = base64encode(<<-EOF
              #!/bin/bash
              echo ECS_CLUSTER=${aws_ecs_cluster.ecs.name} >> /etc/ecs/ecs.config
              EOF
  )
}

# asg

resource "aws_autoscaling_group" "asg" {
  name                = "${var.env}-ecs-asg"
  desired_capacity    = 0
  max_size            = 0
  min_size            = 0
  force_delete               = true
  force_delete_warm_pool     = true
  wait_for_capacity_timeout  = "0"
  vpc_zone_identifier = [aws_subnet.private_1.id, aws_subnet.private_2.id]

  mixed_instances_policy {
    launch_template {
      launch_template_specification {
        launch_template_id = aws_launch_template.ecs.id
        version            = "$Latest"
      }
    }

    instances_distribution {
      on_demand_percentage_above_base_capacity = 0
      spot_allocation_strategy                 = "capacity-optimized"
    }
  }
  lifecycle {
    ignore_changes = [desired_capacity]
  }

    protect_from_scale_in = false 

  tag {
    key                 = "Name"
    value               = "${var.env}-ecs-instance"
    propagate_at_launch = true
  }
}



resource "aws_ecs_capacity_provider" "ecs_capacity_providers" {
  name = "${var.env}-ecs-capacity-provider"

  auto_scaling_group_provider {
    auto_scaling_group_arn = aws_autoscaling_group.asg.arn

    managed_scaling {
      status          = "ENABLED"
      target_capacity = 100
    }

    managed_termination_protection = "DISABLED"
  }
}


#capacity providers

resource "aws_ecs_cluster_capacity_providers" "ecs_capacity_providers" {
  cluster_name = aws_ecs_cluster.ecs.name

  capacity_providers = [
    aws_ecs_capacity_provider.ecs_capacity_providers.name
  ]

  default_capacity_provider_strategy {
    capacity_provider = aws_ecs_capacity_provider.ecs_capacity_providers.name
    weight            = 1
  }
}


#security group

resource "aws_security_group" "ecs_sg" {
  name        = "${var.env}-ecs-sg"
  description = "Security group for ECS instances"
  vpc_id      = aws_vpc.ecs_vpc.id

  ingress {
    description = "SSH access from VPC"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [aws_vpc.ecs_vpc.cidr_block]
  }

  ingress {
    description = "HTTP from ALB"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    security_groups = [aws_security_group.alb_sg.id]
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.env}-ecs-sg"
  }
}



