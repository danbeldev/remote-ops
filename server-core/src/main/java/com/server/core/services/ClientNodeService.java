package com.server.core.services;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ClientNodeService {
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json");
    private static final String COMMAND_URL_TEMPLATE = "http://%s/%s";

    private final OkHttpClient httpClient;
    private final Map<String, String> nodes;

    public ClientNodeService() {
        this(new OkHttpClient());
    }

    public ClientNodeService(OkHttpClient httpClient) {
        this.httpClient = httpClient;
        this.nodes = new ConcurrentHashMap<>();
    }

    public void registerNode(String name, String host, int port) {
        String address = formatAddress(host, port);
        nodes.put(name, address);
        log.info("Node registered: {} -> {}", name, address);
    }

    public void unregisterNode(String name) {
        String address = nodes.remove(name);
        if (address != null) {
            log.info("Node unregistered: {} -> {}", name, address);
        }
    }

    public Set<String> getRegisteredNodeNames() {
        return Set.copyOf(nodes.keySet());
    }

    public void executeCommandOnNode(String command, String nodeName) {
        validateNodeExists(nodeName);
        String url = buildCommandUrl(nodeName, command);
        Request request = buildPostRequest(url);

        try (Response response = httpClient.newCall(request).execute()) {
            handleResponse(response, nodeName, command);
        } catch (Exception e) {
            throw new CommandExecutionException("Failed to execute command on node: " + nodeName, e);
        }
    }

    private String formatAddress(String host, int port) {
        return host + ":" + port;
    }

    private String buildCommandUrl(String nodeName, String command) {
        return String.format(COMMAND_URL_TEMPLATE, nodes.get(nodeName), command);
    }

    private Request buildPostRequest(String url) {
        return new Request.Builder()
                .url(url)
                .post(RequestBody.create("", JSON_MEDIA_TYPE))
                .build();
    }

    private void handleResponse(Response response, String nodeName, String command) {
        if (!response.isSuccessful()) {
            throw new CommandExecutionException(
                    String.format("Command '%s' failed on node '%s': %d - %s",
                            command, nodeName, response.code(), response.message()));
        }
        log.info("Command '{}' executed successfully on node '{}'", command, nodeName);
    }

    private void validateNodeExists(String nodeName) {
        if (!nodes.containsKey(nodeName)) {
            throw new NodeNotFoundException("Node not found: " + nodeName);
        }
    }

    public static class CommandExecutionException extends RuntimeException {
        public CommandExecutionException(String message, Throwable cause) {
            super(message, cause);
        }

        public CommandExecutionException(String message) {
            super(message);
        }
    }

    public static class NodeNotFoundException extends RuntimeException {
        public NodeNotFoundException(String message) {
            super(message);
        }
    }
}