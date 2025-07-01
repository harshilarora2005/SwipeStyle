# 1️⃣ Build Stage: Use Maven to build the jar
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY swipestylebackend/pom.xml .
COPY swipestylebackend/src ./src
RUN mvn clean package -DskipTests

# 2️⃣ Runtime Stage: Alpine + OpenJDK + Chromium + Chromedriver
FROM eclipse-temurin:21-jre-alpine

# Install Chromium and Chromedriver together to ensure version match
RUN apk add --no-cache \
    chromium \
    chromium-chromedriver \
    nss \
    freetype \
    harfbuzz \
    ttf-freefont

# Verify Chromium installation
RUN echo "=== Browser Verification ===" && chromium-browser --version && chromedriver --version

# Create a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Set permissions
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
