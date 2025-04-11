package com.server.core.services;

import com.server.core.configs.RemoteOpsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoteOpsService {

    private final ManagementClientNodeService managementClientNodeService;

    public final RemoteOpsConfig remoteOpsConfig;

    public void remoteOps(String message) {
        var nodeNames = managementClientNodeService.getNodeNames();

        for (RemoteOpsConfig.Command command : remoteOpsConfig.getCommands()) {
            for (String messageTemplate : command.getMessages()) {
                for (String nodeName : nodeNames) {
                    String expectedMessage = messageTemplate.replace("${name}", nodeName);

                    if (message.equalsIgnoreCase(expectedMessage)) {
                        managementClientNodeService.executeCommand(command.getCommand(), nodeName);
                        return;
                    }
                }
            }
        }

        throw new IllegalArgumentException("Команда не распознана или узел не найден");
    }
}
