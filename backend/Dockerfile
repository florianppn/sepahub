# Étape 1 : Compilation du projet avec Maven
FROM maven:3.9-eclipse-temurin-25-alpine AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Image d'exécution minimale
FROM eclipse-temurin:25-alpine

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$PATH:$JAVA_HOME/bin"

# Création d'un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /opt

# Copie de l'artefact compilé depuis l'étape précédente
COPY --from=builder /app/target/sepa26server.jar /opt/sepa26server.jar

USER spring:spring

EXPOSE 8100

ENTRYPOINT ["java", "-jar", "sepa26server.jar"]
