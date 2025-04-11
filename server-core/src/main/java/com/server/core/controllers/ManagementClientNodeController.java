package com.server.core.controllers;

import com.server.core.services.ManagementClientNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/management/client-node")
public class ManagementClientNodeController {

    private final ManagementClientNodeService managementClientNodeService;

    @PostMapping("/connection")
    void connection(
            String name,
            String host,
            Integer port
    ) {
        managementClientNodeService.connection(name, host, port);
    }

    @PostMapping("/{name}/disconnect")
    void disconnect(
        @PathVariable String name
    ) {
        managementClientNodeService.disconnect(name);
    }
}
