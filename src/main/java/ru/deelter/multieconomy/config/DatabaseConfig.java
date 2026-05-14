package ru.deelter.multieconomy.config;

public record DatabaseConfig(String host, int port, String name, String user, String password, int maxPoolSize,
                             int minIdle, long maxLifetime, long keepaliveTime, long connectionTimeout) {
}