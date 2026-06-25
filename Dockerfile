FROM maven:4.0.0-rc-4-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .

RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/app-jar-with-dependencies.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]