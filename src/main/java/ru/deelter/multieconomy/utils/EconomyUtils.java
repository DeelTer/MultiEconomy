package ru.deelter.multieconomy.utils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NonNull;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;

import java.text.DecimalFormat;

public class EconomyUtils {

	public static final TextColor COLOR_RED = TextColor.color(240, 94, 94);

	private static final JoinConfiguration JOIN_CONFIGURATION = JoinConfiguration.builder()
			.separator(Component.text(" "))
			.build();

	public static @NonNull Component getVisual(double amount, @NonNull Currency currency) {
		return Component.join(
				JOIN_CONFIGURATION,
				currency.getIcon(),
				Component.text(formatAmount(amount, currency.getDecimalPlaces()))
		);
	}

	private static String formatAmount(double amount, int decimalPlaces) {
		if (decimalPlaces == 0) {
			return String.valueOf((long) amount);
		}
		StringBuilder pattern = new StringBuilder("#.");
		for (int i = 0; i < decimalPlaces; i++) pattern.append('#');
		DecimalFormat df = new DecimalFormat(pattern.toString());
		return df.format(amount);
	}

	public static @NonNull Sound getDropSingleSound() {
		String key = MultiEconomy.getInstance().getConfigManager().getSoundDropSingle();
		return Sound.sound().type(Key.key(Key.MINECRAFT_NAMESPACE, key)).build();
	}

	public static @NonNull Sound getDropMultiplySound() {
		String key = MultiEconomy.getInstance().getConfigManager().getSoundDropMultiply();
		return Sound.sound().type(Key.key(Key.MINECRAFT_NAMESPACE, key)).build();
	}
}