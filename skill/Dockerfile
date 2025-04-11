FROM eclipse-temurin:22-jdk-jammy as builder

WORKDIR /app
COPY . .

RUN ./gradlew shadowJar

FROM eclipse-temurin:22-jdk-jammy

WORKDIR /app
COPY --from=builder /app/build/libs/remote-ops-skill-1.0-SNAPSHOT-all.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]