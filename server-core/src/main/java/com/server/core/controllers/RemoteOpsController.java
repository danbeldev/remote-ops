package com.server.core.controllers;

import com.server.core.dto.RemoteOperationRequest;
import com.server.core.services.RemoteOpsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/remote-operations")
public class RemoteOpsController {

    private final RemoteOpsService remoteOpsService;

    @Operation(summary = "Execute remote operation", description = "Processes a remote operation message")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Operation accepted for processing"),
            @ApiResponse(responseCode = "400", description = "Invalid request or unknown operation"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void executeRemoteOperation(
            @RequestBody RemoteOperationRequest request
    ) {
        remoteOpsService.processRemoteOperation(request.message());
    }
}