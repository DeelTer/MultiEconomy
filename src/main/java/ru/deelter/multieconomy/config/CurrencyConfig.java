package ru.deelter.multieconomy.config;

public record CurrencyConfig(String id, String name, String iconMiniMessage, double initialBalance, double maxBalance,
                             boolean transferable, double transferFee, boolean primary, int decimalPlaces) {
}