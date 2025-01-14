package com.server.core.controllers;

import com.server.core.client.ClientNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InfoController {

    private final ClientNode clientNode;

    @GetMapping("/os-name")
    public String getOsName() {
        return clientNode.getOsName();
    }
}
