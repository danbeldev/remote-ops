FROM gradle:latest AS builder

WORKDIR /app

COPY build/libs/client-node-0.0.1-SNAPSHOT-all.jar /app/client-node.jar
COPY src/main/resources/client_commands.toml /app/client_commands.toml

EXPOSE 7000

CMD ["java", "-jar", "client-node.jar"]
