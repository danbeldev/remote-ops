package com.server.core.services;

import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ManagementClientNodeService {

    private final OkHttpClient httpClient = new OkHttpClient();

    private final Map<String, String> nodes = new HashMap<>();

    public void connection(
            String name,
            String host,
            Integer port
    ) {
        nodes.put(name, host + ":" + port);
    }

    public void disconnect(String name) {
        nodes.remove(name);
    }

    public Set<String> getNodeNames() {
        return nodes.keySet();
    }

    @SneakyThrows
    public void executeCommand(String command, String nodeName) {
        var nodeAddress = nodes.get(nodeName);
        var url = "http://" + nodeAddress + "/" + command;

        var request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.get("application/json"), ""))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to execute command: " + response.code() + " - " + response.message());
            }
        }
    }
}
