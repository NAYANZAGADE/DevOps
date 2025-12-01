resource "aws_ecs_service" "httpd_service" {
  name            = "httpd"
  cluster         = aws_ecs_cluster.ecs.id
  task_definition = aws_ecs_task_definition.httpd.arn
  desired_count        = 1 
  force_new_deployment = false
  force_delete = true
  


  capacity_provider_strategy {
    capacity_provider = aws_ecs_capacity_provider.ecs_capacity_providers.name
    weight            = 1
  }
  


#   iam_role        = aws_iam_role.foo.arn
#   depends_on      = [aws_iam_role_policy.foo]

#   ordered_placement_strategy {
#     type  = "binpack"
#     field = "cpu"
#   }

#   load_balancer {
#     target_group_arn = var.target_group_arn
#     container_name   = "httpd"
#     container_port   = 80
#   }

  placement_constraints {
    type       = "memberOf"
    expression = "attribute:ecs.availability-zone in [ap-south-1a]"
  }

  depends_on = [aws_autoscaling_group.asg]

  lifecycle {
    create_before_destroy = false
  }

  provisioner "local-exec" {
    when    = destroy
    command = <<-EOT
      aws ecs update-service --cluster ${self.cluster} --service ${self.name} --desired-count 0 --region ap-south-1 || true
      sleep 30
      aws ecs list-tasks --cluster ${self.cluster} --service-name ${self.name} --region ap-south-1 --query 'taskArns[]' --output text | xargs -r -n1 aws ecs stop-task --cluster ${self.cluster} --region ap-south-1 --task || true
    EOT
  }
}