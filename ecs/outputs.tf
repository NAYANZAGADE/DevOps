# Outputs
output "load_balancer_dns" {
  description = "DNS name of the load balancer"
  value       = aws_lb.main.dns_name
}

output "cluster_name" {
  description = "Name of the ECS cluster"
  value       = aws_ecs_cluster.main.name
}

output "keycloak_service_name" {
  description = "Name of the Keycloak ECS service"
  value       = aws_ecs_service.keycloak.name
}

output "redis_service_name" {
  description = "Name of the Redis ECS service"
  value       = aws_ecs_service.redis.name
}

output "keycloak_admin_url" {
  description = "Keycloak admin console URL"
  value       = "http://${aws_lb.main.dns_name}/admin"
}

output "keycloak_admin_credentials" {
  description = "Keycloak admin credentials"
  value       = "Username: ${var.keycloak_admin_user}, Password: ${var.keycloak_admin_password}"
  sensitive   = true
}
# Database Outputs
output "rds_endpoint" {
  description = "RDS instance endpoint"
  value       = aws_db_instance.postgres.endpoint
}

output "rds_port" {
  description = "RDS instance port"
  value       = aws_db_instance.postgres.port
}

output "database_name" {
  description = "Name of the database"
  value       = aws_db_instance.postgres.db_name
}

output "database_username" {
  description = "Database master username"
  value       = aws_db_instance.postgres.username
  sensitive   = true
}

output "database_connection_string" {
  description = "Database connection string for applications"
  value       = "postgresql://${aws_db_instance.postgres.username}:${var.db_password}@${aws_db_instance.postgres.endpoint}/${aws_db_instance.postgres.db_name}"
  sensitive   = true
}
