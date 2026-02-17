# ---------- STAGE 1: BUILD ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia apenas pom primeiro (cache de dependências)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o resto do código
COPY src ./src

# Gera o jar
RUN mvn clean package -DskipTests


# ---------- STAGE 2: RUNTIME ----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia o jar gerado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]