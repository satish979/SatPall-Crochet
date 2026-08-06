# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -DskipTests dependency:go-offline

COPY src ./src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:8-jre
WORKDIR /app

COPY --from=build /workspace/target/loomellecrochet-1.0.0.jar /app/target/loomellecrochet-1.0.0.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/loomellecrochet-1.0.0.jar"]
