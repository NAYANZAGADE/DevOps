# Spring Boot Profiles Configuration

This project uses Spring Boot profiles to manage different environment configurations.

## How to Use

### Running with Local Profile (Default)
```bash
# Explicitly specify local profile
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Running with Dev Profile
```bash
# Default behavior (uses dev profile)
./gradlew bootRun

# Run with dev profile
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## File Structure
```
src/main/resources/
├── application.properties          # Base configuration (defaults)
├── application-local.properties    # Local development overrides
└── application-dev.properties      # Development environment overrides
```

## Adding New Profiles
To add a new profile (e.g., `prod`):
1. Create `application-prod.properties`
2. Add environment-specific configurations
3. Use environment variables for sensitive data
4. Update this documentation 

## Sample application-local.properties

```
# Local Development Profile
# Server Configuration
server.port=9090

# DataSource Configuration (PostgreSQL from Docker Compose)
# Use localhost for local development
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=postgres
spring.datasource.password=Postgres@123
spring.datasource.driver-class-name=org.postgresql.Driver

# Encryption key (must be 16 characters for AES-128)
encryption.secret.key=abcdefghijklmnop

# Finch OAuth Settings (Sandbox)
finch.client-id=5436f7c6-ad68-4626-a460-ac3ef232ac0e
finch.client-secret=finch-secret-sandbox-4TaBVJOrtdAvJ98DqhAYVO24f0R5gvhDOjFWpXhJ
finch.redirect-uri=http://localhost:3000/finch/callback
finch.base-url=https://connect.tryfinch.com/authorize/
finch.token-url=https://api.tryfinch.com/auth/token
finch.products=company

# Keycloak Configuration (Local Docker)
keycloak.auth-server-url=http://localhost:8080
keycloak.admin.username=admin
keycloak.admin.password=admin
keycloak.admin.realm=master
keycloak.admin.client-id=admin-cli

# Logging Configuration
logging.level.org.springframework.web=DEBUG
logging.level.com.glidingpath=DEBUG
logging.level.org.flywaydb.core=DEBUG

# Docker Compose (enabled for local development)
spring.docker.compose.enabled=true 
```