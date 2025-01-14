package com.client.node.commands.os;

import lombok.Getter;

@Getter
public enum OS {
    WINDOWS("windows"),
    LINUX("linux"),
    MACOS("macos"),;

    private final String name;

    OS(String name) {
        this.name = name;
    }
}
