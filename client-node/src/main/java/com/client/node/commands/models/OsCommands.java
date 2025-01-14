package com.client.node.commands.models;

import java.util.Map;

public record OsCommands(
        Map<String, String> commandsTypeGet,
        Map<String, String> commandsTypePost
) {}
