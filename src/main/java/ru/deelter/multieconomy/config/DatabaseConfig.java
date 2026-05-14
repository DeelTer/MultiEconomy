package ru.deelter.multieconomy.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DatabaseConfig {
    private final String host;
    private final int port;
    private final String name;
    private final String user;
    private final String password;
    private final int maxPoolSize;
    private final int minIdle;
    private final long maxLifetime;
    private final long keepaliveTime;
    private final long connectionTimeout;
}