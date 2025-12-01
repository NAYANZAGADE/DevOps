resource "aws_ecs_service" "httpd_service" {
  name            = "httpd"
  cluster         = aws_ecs_cluster.ecs.id
  task_definition = aws_ecs_task_definition.httpd.arn
  desired_count   = 1 
  force_new_deployment = true

#   capacity_provider_strategy {
#     capacity_provider = aws_ecs_capacity_provider.ecs_capacity_providers.name
#     weight            = 1
#   }

  launch_type = "EC2"
  


#   iam_role        = aws_iam_role.foo.arn
#   depends_on      = [aws_iam_role_policy.foo]

#   ordered_placement_strategy {
#     type  = "binpack"
#     field = "cpu"
#   }

  load_balancer {
    target_group_arn = aws_lb_target_group.instance_tg.arn
    container_name   = "httpd"
    container_port   = 80
  }

  placement_constraints {
    type       = "memberOf"
    expression = "attribute:ecs.availability-zone in [ap-south-1a]"
  }
}