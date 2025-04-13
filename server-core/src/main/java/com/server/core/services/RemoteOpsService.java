package com.server.core.services;

import com.server.core.configs.RemoteOpsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteOpsService {
    private final ClientNodeService clientNodeService;
    private final RemoteOpsConfig remoteOpsConfig;

    public void processRemoteOperation(String message) {
        findMatchingCommand(message)
                .ifPresentOrElse(
                        this::executeCommandOnAllNodes,
                        () -> handleUnknownCommand(message)
                );
    }

    private Optional<RemoteOpsConfig.Command> findMatchingCommand(String message) {
        return remoteOpsConfig.getCommands().stream()
                .filter(command -> matchesAnyMessageTemplate(command, message))
                .findFirst();
    }

    private boolean matchesAnyMessageTemplate(RemoteOpsConfig.Command command, String message) {
        return command.getMessages().stream()
                .anyMatch(template -> matchesMessage(template, message));
    }

    private boolean matchesMessage(String template, String message) {
        return clientNodeService.getRegisteredNodeNames().stream()
                .anyMatch(nodeName ->
                        message.equalsIgnoreCase(template.replace("${name}", nodeName))
                );
    }

    private void executeCommandOnAllNodes(RemoteOpsConfig.Command command) {
        clientNodeService.getRegisteredNodeNames().forEach(nodeName -> {
            try {
                clientNodeService.executeCommandOnNode(command.getCommand(), nodeName);
            } catch (ResponseStatusException e) {
                log.warn("Failed to execute command '{}' on node '{}'", command.getCommand(), nodeName, e);
            }
        });
    }

    private void handleUnknownCommand(String message) {
        log.warn("Unknown command or no matching nodes found for message: {}", message);
        throw new IllegalArgumentException("Command not recognized or no matching nodes found");
    }
}