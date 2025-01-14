package com.server.core.controllers;

import com.server.core.client.ClientNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PowerController {

    private final ClientNode clientNode;

    @GetMapping("/shutdown")
    public void shutdown() {
        clientNode.shutdown();
    }

    @GetMapping("/reboot")
    public void reboot() {
        clientNode.reboot();
    }

    @GetMapping("/sleep")
    public void sleep() {
        clientNode.sleep();
    }
}

