package ru.deelter.multieconomy.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurrencyConfig {
    private final String id;
    private final String name;
    private final String iconMiniMessage;
    private final String color;
    private final double initialBalance;
    private final double maxBalance;
    private final boolean transferable;
    private final double transferFee;
    private final boolean primary;
    private final int decimalPlaces;
}