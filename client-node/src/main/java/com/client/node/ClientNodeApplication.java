package com.client.node;

import com.client.node.commands.ParserCommand;
import com.client.node.commands.impl.TomlParserCommand;
import com.client.node.commands.os.OSUtils;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class ClientNodeApplication {

    public static void main(String[] args) {
        ParserCommand parser = new TomlParserCommand();
        var osCommands = parser.parse("/client_commands.toml", OSUtils.getOperatingSystem());

        var app = Javalin.create().start("0.0.0.0", 7000);

        handleGetCommands(app, osCommands.commandsTypeGet());
        handlePostCommands(app, osCommands.commandsTypePost());
    }

    private static void handleGetCommands(Javalin app, Map<String, String> command) {
        for (Map.Entry<String, String> entry : command.entrySet()) {
            app.get(entry.getKey(), ctx -> handleGetCommand(ctx, entry.getValue()));
        }
    }

    private static void handlePostCommands(Javalin app, Map<String, String> command) {
        for (Map.Entry<String, String> entry : command.entrySet()) {
            app.post(entry.getKey(), ctx -> handlePostCommand(ctx, entry.getValue()));
        }
    }

    private static void handleGetCommand(Context ctx, String command) {
        try {
            var processBuilder = new ProcessBuilder(command.split(" "));
            var process = processBuilder.start();

            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    ctx.result(output.toString());
                } else {
                    ctx.result("Ошибка при выполнении команды:\n" + output);
                }
            }
        } catch (IOException | InterruptedException e) {
            ctx.result("Исключение: " + e.getMessage());
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
}
