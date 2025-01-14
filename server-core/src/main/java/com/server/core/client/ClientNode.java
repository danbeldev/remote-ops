package com.server.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "client-node", url = "${feign.clients.main.url}")
public interface ClientNode {

    @GetMapping("os-name")
    String getOsName();

    @PostMapping("shutdown")
    void shutdown();

    @PostMapping("reboot")
    void reboot();

    @PostMapping("sleep")
    void sleep();
}
