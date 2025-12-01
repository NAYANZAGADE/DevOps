# asg

resource "aws_autoscaling_group" "asg" {
  name                = "${var.env}-ecs-asg"
  desired_capacity    = 2
  max_size            = 2
  min_size            = 2
  force_delete               = true
  force_delete_warm_pool     = true
  wait_for_capacity_timeout  = "0"
  termination_policies       = ["OldestInstance"]
  default_cooldown          = 300
  health_check_grace_period = 300
  health_check_type         = "EC2"
  vpc_zone_identifier = var.private_subnet_ids

  mixed_instances_policy {
    launch_template {
      launch_template_specification {
        launch_template_id = aws_launch_template.ecs.id
        version            = "$Latest"
      }
    }

    instances_distribution {
      on_demand_percentage_above_base_capacity = 100
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