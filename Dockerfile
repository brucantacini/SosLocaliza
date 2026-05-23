# Imagem de runtime — o JAR é gerado na etapa Maven da pipeline (azure-pipelines.yml).
# O .dockerignore exclui mvnw/.mvn e inclui apenas target/SosLocaliza-0.0.1-SNAPSHOT.jar.

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

COPY target/SosLocaliza-0.0.1-SNAPSHOT.jar app.jar
RUN chown spring:spring app.jar

USER spring:spring
EXPOSE 8080
ENV SERVER_PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]
