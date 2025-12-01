@echo off
echo Building Spring Boot application using Gradle...
call gradlew clean build

IF %ERRORLEVEL% NEQ 0 (
    echo Build failed. Exiting...
    exit /b %ERRORLEVEL%
)

echo Starting Docker Compose...
docker-compose up --build
