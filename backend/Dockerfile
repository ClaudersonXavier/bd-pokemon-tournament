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
RUN set -eux; \
    jar="$(find target -maxdepth 1 -type f -name '*.jar' ! -name 'original-*.jar' | head -n 1)"; \
    test -n "$jar"; \
    cp "$jar" /app/app.jar


# ---------- STAGE 2: RUNTIME ----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia o jar gerado
COPY --from=build /app/app.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
