package com.client.node;

import com.client.node.commands.ParserCommand;
import com.client.node.commands.impl.TomlParserCommand;
import com.client.node.commands.os.OSUtils;
import com.client.node.server.ServerCore;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ClientNodeApplication {
    private static final Logger logger = LoggerFactory.getLogger(ClientNodeApplication.class);

    private static AppConfig config;
    private static ServerCore serverCore;
    private static Javalin app;

    public static void main(String[] args) {
        try {
            initializeApplication();
            startApplication();
        } catch (Exception e) {
            logger.error("Application failed to start", e);
            System.exit(1);
        }
    }

    private static void initializeApplication() throws IOException {
        config = loadConfig();
        serverCore = createServerCore(config.serverBaseUrl());
        registerShutdownHook();
    }

    private static void startApplication() {
        connectToServer(() -> {
            ParserCommand parser = new TomlParserCommand();
            var osCommands = parser.parse("/client_commands.toml", OSUtils.getOperatingSystem());

            app = createAndStartJavalinApp(config.port());
            registerPostCommands(app, osCommands.commandsTypePost());
        });
    }

    private static AppConfig loadConfig() throws IOException {
        var yaml = new Yaml();
        try (InputStream input = new FileInputStream("config.yml")) {
            Map<String, Object> config = yaml.load(input);
            return new AppConfig(
                    (String) config.get("name"),
                    (String) config.get("host"),
                    (Integer) config.get("port"),
                    (String) config.get("server-base-url")
            );
        }
    }

    private static ServerCore createServerCore(String serverBaseUrl) {
        return new Retrofit.Builder()
                .baseUrl(serverBaseUrl)
                .build()
                .create(ServerCore.class);
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.info("Shutting down, sending disconnect...");
                Response<Void> response = serverCore.unregisterNode(config.name()).execute();
                logger.info("Disconnect: {}", response.isSuccessful() ? "success" : "failed");
            } catch (IOException e) {
                logger.error("Error during disconnect", e);
            }
        }));
    }

    private static Javalin createAndStartJavalinApp(int port) {
        return Javalin.create().start("0.0.0.0", port);
    }

    private static void registerPostCommands(Javalin app, Map<String, String> commands) {
        commands.forEach((path, command) ->
                app.post(path, ctx -> executeCommand(ctx, command))
        );
    }

    private static void executeCommand(Context ctx, String command) {
        try {
            Process process = new ProcessBuilder(command.split(" ")).start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                ctx.result("Command executed successfully: " + command);
                logger.info("Command executed: {}", command);
            } else {
                ctx.result("Command execution failed: " + command);
                logger.warn("Command failed with exit code {}: {}", exitCode, command);
            }
        } catch (IOException | InterruptedException e) {
            ctx.result("Exception: " + e.getMessage());
            logger.error("Error executing command: {}", command, e);
            Thread.currentThread().interrupt();
        }
    }

    private static void connectToServer(Runnable onSuccess) {
        serverCore.registerNode(config.name(), config.host(), config.port()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                logger.info("Server connection response: {}", response.code());
                if (response.isSuccessful()) {
                    onSuccess.run();
                }else {
                    System.exit(1);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                logger.error("Failed to connect to server", t);
            }
        });
    }

    private record AppConfig(String name, String host, int port, String serverBaseUrl) {}
}