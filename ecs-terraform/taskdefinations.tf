resource "aws_ecs_task_definition" "httpd" {
  family = "httpd"
  container_definitions = jsonencode([
    {
      name      = "httpd"
      image     = "httpd:latest"
      cpu       = 10
      memory    = 512
      essential = true
      portMappings = [
        {
          containerPort = 80
          hostPort      = 80
        }
      ]
    },
  ])
  network_mode = "bridge"


  placement_constraints {
    type       = "memberOf"
    expression = "attribute:ecs.availability-zone in [ap-south-1a]"
  }
}


