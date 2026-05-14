package ru.deelter.multieconomy.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.kyori.adventure.text.Component;
import ru.deelter.multieconomy.data.Currency;

import static ru.deelter.multieconomy.utils.EconomyUtils.COLOR_RED;

@Data
@AllArgsConstructor
public class VisualOperation {

	private static final double DELTA_FACTOR = 0.1;
	private static final double EPSILON = 0.05;

	private final Currency currency;
	private double currentBalance;
	private double newBalance;
	private final double maxBalance;

	public Component toComponent() {
		Component component = EconomyUtils.getVisual(currentBalance, currency);
		if (currentBalance >= maxBalance) {
			component = component.append(Component.text(" / " + maxBalance).color(COLOR_RED));
		}
		return component;
	}

	public void update() {
		double delta = determineValue(currentBalance, newBalance);
		if (currentBalance > newBalance) {
			currentBalance -= delta;
		} else {
			currentBalance += delta;
		}
	}

	public boolean isCompleted() {
		return Math.abs(newBalance - currentBalance) < EPSILON;
	}

	private double determineValue(double currentValue, double targetValue) {
		return Math.max(DELTA_FACTOR, Math.abs(targetValue - currentValue) / 10);
	}
}