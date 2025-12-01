# CloudWatch Log Groups
resource "aws_cloudwatch_log_group" "keycloak" {
  name              = "/ecs/keycloak-app"
  retention_in_days = 7

  tags = {
    Name = "ecs-keycloak-logs"
  }
}

resource "aws_cloudwatch_log_group" "redis" {
  name              = "/ecs/redis-app"
  retention_in_days = 7

  tags = {
    Name = "ecs-redis-logs"
  }
}

# Redis Task Definition
resource "aws_ecs_task_definition" "redis" {
  family                   = "redis-app"
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]

  container_definitions = jsonencode([
    {
      name  = "redis"
      image = "redis:7-alpine"

      portMappings = [
        {
          containerPort = 6379
          hostPort      = 6379
          protocol      = "tcp"
        }
      ]

      memory = 256
      cpu    = 256

      essential = true

      environment = [
        {
          name  = "REDIS_PASSWORD"
          value = "redis123"
        }
      ]

      command = ["redis-server", "--requirepass", "redis123"]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.redis.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "redis"
        }
      }
    }
  ])

  tags = {
    Name = "redis-task-definition"
  }
}

# Keycloak Task Definition
resource "aws_ecs_task_definition" "keycloak" {
  family                   = "keycloak-app"
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]

  container_definitions = jsonencode([
    {
      name  = "keycloak"
      image = "quay.io/keycloak/keycloak:25.0"

      portMappings = [
        {
          containerPort = 8080
          hostPort      = 0
          protocol      = "tcp"
        }
      ]

      memory = 1024
      cpu    = 512

      essential = true

      environment = [
        {
          name  = "KEYCLOAK_ADMIN"
          value = var.keycloak_admin_user
        },
        {
          name  = "KEYCLOAK_ADMIN_PASSWORD"
          value = var.keycloak_admin_password
        },
        {
          name  = "KC_HOSTNAME_STRICT"
          value = "false"
        },
        {
          name  = "KC_HOSTNAME_STRICT_HTTPS"
          value = "false"
        },
        {
          name  = "KC_HTTP_ENABLED"
          value = "true"
        },
        {
          name  = "KC_HEALTH_ENABLED"
          value = "true"
        },
        {
          name  = "KC_DB"
          value = "postgres"
        },
        {
          name  = "KC_DB_URL"
          value = "jdbc:postgresql://ecs-postgres-db.c1cy6wak2qua.ap-south-1.rds.amazonaws.com:5432/keycloak"
        },
        {
          name  = "KC_DB_USERNAME"
          value = var.db_username
        },
        {
          name  = "KC_DB_PASSWORD"
          value = var.db_password
        }
      ]

      command = ["start-dev"]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.keycloak.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "keycloak"
        }
      }
    }
  ])

  tags = {
    Name = "keycloak-task-definition"
  }
}

# Redis Service
resource "aws_ecs_service" "redis" {
  name            = "redis-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.redis.arn
  desired_count   = 1

  tags = {
    Name = "redis-service"
  }
}

# Keycloak Service
resource "aws_ecs_service" "keycloak" {
  name            = "keycloak-service-dev"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.keycloak.arn
  desired_count   = 1
  
  # Health check grace period for container startup
  health_check_grace_period_seconds = 300

  load_balancer {
    target_group_arn = aws_lb_target_group.keycloak.arn
    container_name   = "keycloak"
    container_port   = 8080
  }

  depends_on = [aws_lb_listener.keycloak, aws_ecs_service.redis, aws_db_instance.postgres]

  tags = {
    Name = "keycloak-service"
  }
}

# RDI Task Definition (if needed as separate service)
resource "aws_ecs_task_definition" "rdi" {
  family                   = "rdi-app"
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]

  container_definitions = jsonencode([
    {
      name  = "rdi"
      image = "redislabs/rdi:latest"

      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8081
          protocol      = "tcp"
        }
      ]

      memory = 512
      cpu    = 256

      essential = true

      environment = [
        {
          name  = "REDIS_HOST"
          value = "localhost"
        },
        {
          name  = "REDIS_PORT"
          value = "6379"
        },
        {
          name  = "REDIS_PASSWORD"
          value = "redis123"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.redis.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "rdi"
        }
      }
    }
  ])

  tags = {
    Name = "rdi-task-definition"
  }
}