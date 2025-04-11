package com.server.core.controllers;

import com.server.core.services.RemoteOpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/remote-ops")
public class RemoteOpsController {

    private final RemoteOpsService remoteOpsService;

    @PostMapping
    public void remoteOps(
            String message
    ) {
        remoteOpsService.remoteOps(message);
    }
}

