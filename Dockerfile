# Imagem de runtime — a pipeline copia target/*.jar para app.jar antes do docker build.
# Local: mvn package -DskipTests && cp target/SosLocaliza-0.0.1-SNAPSHOT.jar app.jar

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

# app.jar: cópia na pipeline CD; target/...: build local ou CI com Maven
COPY app.jar app.jar
RUN chown spring:spring app.jar

USER spring:spring
EXPOSE 8080
ENV SERVER_PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]
