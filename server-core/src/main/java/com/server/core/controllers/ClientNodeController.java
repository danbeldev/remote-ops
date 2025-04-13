package com.server.core.controllers;

import com.server.core.services.ClientNodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/client-nodes")
public class ClientNodeController {

    private final ClientNodeService clientNodeService;

    @Operation(summary = "Register client node", description = "Registers a new client node for remote operations")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Node registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid node parameters"),
            @ApiResponse(responseCode = "409", description = "Node with this name already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registerNode(
            @RequestParam String name,
            HttpServletRequest request
    ) {
        String host = request.getRemoteAddr();
        int port = request.getRemotePort();
        clientNodeService.registerNode(name, host, port);
    }

    @Operation(summary = "Unregister client node", description = "Removes a client node from registry")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Node unregistered successfully"),
            @ApiResponse(responseCode = "404", description = "Node not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregisterNode(
            @PathVariable String name
    ) {
        clientNodeService.unregisterNode(name);
    }
}