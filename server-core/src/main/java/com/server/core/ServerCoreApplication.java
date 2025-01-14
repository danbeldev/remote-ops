package com.server.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ServerCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerCoreApplication.class, args);
    }

}
