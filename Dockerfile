# syntax=docker/dockerfile:1

# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-8 AS build

WORKDIR /workspace

# Copy Maven wrapper and configuration
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:8-jre

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh","-c","java -Dserver.port=$PORT -jar app.jar"]