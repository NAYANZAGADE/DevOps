# ---------- build stage ----------
FROM gradle:9-jdk-jammy AS build

# Use /home/gradle/project to match gradle image user
WORKDIR /home/gradle/project

# Copy the entire project (use .dockerignore to keep context small)
COPY --chown=gradle:gradle . .

# Ensure wrapper is executable (harmless if not needed)
RUN if [ -f ./gradlew ]; then chmod +x ./gradlew; fi

# Build fat jar (Spring Boot's bootJar) and skip tests to speed up; remove -x test if you want tests
RUN ./gradlew --no-daemon clean bootJar -x test

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jdk-jammy AS runtime
WORKDIR /app

# Copy the fat jar from build stage. This wildcard will pick the built Spring Boot jar.
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

# Optional: create a non-root user (safer in production)
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser && chown appuser:appgroup /app
USER appuser

EXPOSE 9090

# JVM args can be provided via CMD or as container args; keep ENTRYPOINT simple
ENTRYPOINT ["java", "-jar", "/app/app.jar"]