package ru.deelter.multieconomy.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;

import java.text.DecimalFormat;

public class EconomyUtils {

    public static final TextColor COLOR_RED = TextColor.color(240, 94, 94);
    public static final Sound SOUND_COINS_DROP_SINGLE = Sound.sound()
            .type(Key.key(Key.MINECRAFT_NAMESPACE, "custom.coins.drop.single"))
            .build();
    public static final Sound SOUND_COINS_DROP_MULTIPLY = Sound.sound()
            .type(Key.key(Key.MINECRAFT_NAMESPACE, "custom.coins.drop.multiply"))
            .build();

    private static final JoinConfiguration JOIN_CONFIGURATION = JoinConfiguration.builder()
            .separator(Component.text(" "))
            .build();

    public static Sound getSound(double value) {
        return value > 1 ? SOUND_COINS_DROP_SINGLE : SOUND_COINS_DROP_MULTIPLY;
    }

    public static @NotNull Component getVisual(double amount, @NotNull Currency currency, @NotNull Audience audience) {
        Component numberComponent = Component.text(formatAmount(amount, currency.getDecimalPlaces()));
        if (currency.getColor() != null) {
            numberComponent = numberComponent.color(currency.getColor());
        }
        return Component.join(
                JOIN_CONFIGURATION,
                MultiEconomy.getInstance().getLang().parseMessage(currency.getIconMiniMessage(), audience),
                numberComponent
        );
    }

    public static String formatAmount(double amount, int decimalPlaces) {
        if (decimalPlaces == 0) {
            return String.valueOf((long) amount);
        }
        StringBuilder pattern = new StringBuilder("#.");
        for (int i = 0; i < decimalPlaces; i++) {
            pattern.append('#');
        }
        DecimalFormat decimalFormat = new DecimalFormat(pattern.toString());
        return decimalFormat.format(amount);
    }

    public static @NotNull Sound getDropSingleSound() {
        String key = MultiEconomy.getInstance().getConfigManager().getSoundDropSingle();
        return Sound.sound().type(Key.key(Key.MINECRAFT_NAMESPACE, key)).build();
    }

    public static @NotNull Sound getDropMultiplySound() {
        String key = MultiEconomy.getInstance().getConfigManager().getSoundDropMultiply();
        return Sound.sound().type(Key.key(Key.MINECRAFT_NAMESPACE, key)).build();
    }
}