resource "aws_launch_template" "ecs" {
  name_prefix   = "${var.env}-ecs-lt"
  instance_type = var.instance_type
  image_id      = data.aws_ami.ecs_optimized.id
  key_name      = var.keyname

  iam_instance_profile {
    name = var.ecs_instance_profile_name
  }

  vpc_security_group_ids = [var.ecs_sg_id]

  user_data = base64encode(<<-EOF
#!/bin/bash
echo ECS_CLUSTER=${aws_ecs_cluster.ecs.name} >> /etc/ecs/ecs.config
start ecs
EOF
  )
}