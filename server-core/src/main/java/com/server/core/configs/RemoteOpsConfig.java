package com.server.core.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "remote-ops")
public class RemoteOpsConfig {
    private List<Command> commands;

    @Getter
    @Setter
    public static class Command {
        private List<String> messages;
        private String command;
    }
}
