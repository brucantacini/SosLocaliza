# Dockerfile para SOS Localiza - Spring Boot Application
# Usando imagem Alpine Linux para melhor desempenho (requisito: imagens slim/Alpine)

FROM eclipse-temurin:21-jre-alpine

# Criar grupo e usuário sem privilégios administrativos (requisito: usuário não-root)
RUN addgroup -S spring && adduser -S spring -G spring

# Definir diretório padrão para o projeto (requisito: diretório padrão)
WORKDIR /app

# Copiar o JAR da aplicação
COPY target/SosLocaliza-0.0.1-SNAPSHOT.jar app.jar

# Mudar propriedade do arquivo para o usuário não-root
RUN chown spring:spring app.jar

# Mudar para usuário não-root (requisito: executar sem privilégios administrativos)
USER spring:spring

# Expor a porta da aplicação
EXPOSE 8081

# Executar a aplicação em background (requisito: projeto em background)
ENTRYPOINT ["java", "-jar", "app.jar"]

