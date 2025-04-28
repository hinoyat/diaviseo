FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build -x test

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]