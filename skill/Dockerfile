FROM eclipse-temurin:22-jdk-jammy as builder

WORKDIR /app
COPY . .

RUN ./gradlew build --no-daemon

FROM eclipse-temurin:22-jdk-jammy

WORKDIR /app
COPY --from=builder /app/build/libs/ktor-server.jar /app/ktor-server.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ktor-server.jar"]