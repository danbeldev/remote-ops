package com.client.node;

import com.client.node.commands.ParserCommand;
import com.client.node.commands.impl.TomlParserCommand;
import com.client.node.commands.os.OSUtils;
import com.client.node.server.ServerCore;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.Map;

public class ClientNodeApplication {

    private static final String name = "Компьютер";
    private static final String host = "127.0.0.1";
    private static final Integer port = 7000;

    private static final String serverBaseUrl = "http://localhost:8080";

    private static ServerCore serverCore;

    public static void main(String[] args) {
        serverCore = createServerCore();

        disconnectServerCore();

        connectionServerCore(() -> {
            ParserCommand parser = new TomlParserCommand();
            var osCommands = parser.parse("/client_commands.toml", OSUtils.getOperatingSystem());

            var app = Javalin.create().start("0.0.0.0", port);

            handlePostCommands(app, osCommands.commandsTypePost());
        });
    }

    private static void disconnectServerCore() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Завершается работа, отправка disconnect...");
                Response<Void> response = serverCore.disconnect(name).execute();
                System.out.println("Отключение: " + (response.isSuccessful() ? "успешно" : "ошибка"));
            } catch (IOException e) {
                System.err.println("Ошибка при disconnect: " + e.getMessage());
            }
        }));
    }

    private static void handlePostCommands(Javalin app, Map<String, String> command) {
        for (Map.Entry<String, String> entry : command.entrySet()) {
            app.post(entry.getKey(), ctx -> handlePostCommand(ctx, entry.getValue()));
        }
    }

    private static void handlePostCommand(Context ctx, String command) {
        try {
            var processBuilder = new ProcessBuilder(command.split(" "));
            var process = processBuilder.start();
            if (process.waitFor() == 0) {
                ctx.result("Команда выполнена успешно: " + command);
            } else {
                ctx.result("Ошибка при выполнении команды: " + command);
            }
        } catch (IOException | InterruptedException e) {
            ctx.result("Исключение: " + e.getMessage());
        }
    }

    private static void connectionServerCore(
            Runnable runnable
    ) {
        serverCore.connect(name, host, port).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                runnable.run();
            }

            @Override
            public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private static ServerCore createServerCore() {
        return new Retrofit.Builder()
                .baseUrl(serverBaseUrl)
                .build()
                .create(ServerCore.class);
    }
}
