Write-Host "Building Spring Boot application using Gradle..."
./gradlew clean build

if ($LASTEXITCODE -ne 0) {
    Write-Host "Gradle build failed. Exiting..."
    exit $LASTEXITCODE
}

Write-Host "Starting Docker Compose..."
docker-compose up --build