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

resource "aws_ecs_cluster_capacity_providers" "aws_ecs_cluster_capacity_providers" {
  cluster_name = aws_ecs_cluster.ecs.name

  capacity_providers = [
    aws_ecs_capacity_provider.ecs_capacity_providers.name
  ]

  default_capacity_provider_strategy {
    capacity_provider = aws_ecs_capacity_provider.ecs_capacity_providers.name
    weight            = 1
  }
}