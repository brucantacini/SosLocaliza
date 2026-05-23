# Build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src src
RUN chmod +x mvnw && ./mvnw -q -DskipTests package

# Runtime
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=build /app/target/SosLocaliza-0.0.1-SNAPSHOT.jar app.jar
RUN chown spring:spring app.jar
USER spring:spring
EXPOSE 8080
ENV SERVER_PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]
