package ru.deelter.multieconomy.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.kyori.adventure.text.Component;
import ru.deelter.multieconomy.data.Currency;

@Data
@AllArgsConstructor
public class VisualOperation {

    private static final double EPSILON = 0.05;

    private final Currency currency;
    private double currentBalance;
    private double newBalance;
    private final double maxBalance;

    public Component toComponent() {
        Component component = EconomyUtils.getVisual(currentBalance, currency);
        if (currentBalance >= maxBalance) {
            component = component.append(Component.text(" / " + formatMax()).color(EconomyUtils.COLOR_RED));
        }
        return component;
    }

    private String formatMax() {
        if (currency.getDecimalPlaces() == 0) {
            return String.valueOf((long) maxBalance);
        }
        String pattern = "%." + currency.getDecimalPlaces() + "f";
        return String.format(pattern, maxBalance);
    }

    public void update() {
        double delta = determineValue(currentBalance, newBalance);
        if (currentBalance > newBalance) {
            currentBalance -= delta;
        } else {
            currentBalance += delta;
        }
        if (Math.abs(newBalance - currentBalance) < delta) {
            currentBalance = newBalance;
        }
    }

    public boolean isCompleted() {
        return Math.abs(newBalance - currentBalance) < EPSILON;
    }

    private double determineValue(double a, double b) {
        return Math.max(0.1, Math.abs(a - b) / 10);
    }
}