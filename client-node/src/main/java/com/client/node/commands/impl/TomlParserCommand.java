package com.client.node.commands.impl;

import com.client.node.commands.ParserCommand;
import com.client.node.commands.models.OsCommands;
import com.client.node.commands.os.OS;
import com.moandjiezana.toml.Toml;

import java.util.HashMap;
import java.util.Map;

public class TomlParserCommand implements ParserCommand {
    @Override
    public OsCommands parse(String path, OS os) {
        var inputStream = getClass().getResourceAsStream(path);

        if (inputStream == null) {
            throw new RuntimeException("Unable to find file " + path);
        }

        var toml = new Toml().read(inputStream);

        return new OsCommands(
                getCommands(toml, os, ".get"),
                getCommands(toml, os, ".post")
        );
    }

    private Map<String, String> getCommands(Toml toml, OS os, String prefix) {
        var commands = new HashMap<String, String>();
        toml.getTable(os.getName() + prefix).toMap().forEach((key, value) -> {
            commands.put(key, value.toString());
        });
        return commands;
    }
}
