# ---- Build stage ----
# Build context is the repo ROOT (see docker-compose.yml), not this folder, because
# this module's pom.xml inherits from the parent pom.xml one level up - Maven needs
# that parent visible in the build context or dependency resolution fails immediately.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY user-service/pom.xml user-service/pom.xml
COPY user-service/src user-service/src

WORKDIR /app/user-service
RUN mvn clean package -DskipTests

# ---- Run stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/user-service/target/user-service.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
