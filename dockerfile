# 🧩 Etapa 1: construir el JAR con Maven y Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos archivos del proyecto
COPY pom.xml .
COPY src ./src

# Compilamos y empaquetamos la aplicación (sin ejecutar tests)
RUN mvn clean package -DskipTests

# 🧩 Etapa 2: imagen final ligera para ejecución
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiamos el jar generado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto (ajústalo si tu app usa otro)
EXPOSE 8080

# Ejecutamos la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
