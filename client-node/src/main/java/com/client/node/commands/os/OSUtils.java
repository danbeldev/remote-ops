package com.client.node.commands.os;

public class OSUtils {
    public static OS getOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return OS.WINDOWS;
        } else if (os.contains("nix") || os.contains("nux")) {
            return OS.LINUX;
        } else if (os.contains("mac")) {
            return OS.MACOS;
        } else {
            throw new UnsupportedOperationException("Unsupported Operating System: " + os);
        }
    }
}
