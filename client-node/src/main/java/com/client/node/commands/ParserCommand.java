package com.client.node.commands;

import com.client.node.commands.models.OsCommands;
import com.client.node.commands.os.OS;

public interface ParserCommand {

    OsCommands parse(String path, OS os);
}
