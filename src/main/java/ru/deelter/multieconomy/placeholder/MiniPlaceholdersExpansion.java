package ru.deelter.multieconomy.placeholder;

import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;

public class MiniPlaceholdersExpansion {

    public static void register(@NonNull MultiEconomy plugin) {
        Expansion.Builder builder = Expansion.builder("multieconomy");

        for (Currency currency : plugin.getEconomyManager().getCurrencies().values()) {
            final Currency c = currency;
            builder.audiencePlaceholder(Player.class, c.getId(), (player, queue, ctx) -> {
                double balance = plugin.getEconomyManager().getBalance(player.getUniqueId(), c.getId());
                Component text = Component.text(formatBalance(balance, c.getDecimalPlaces()));
                if (c.getColor() != null) {
                    text = text.style(Style.style(c.getColor()));
                }
                return Tag.selfClosingInserting(text);
            });
        }

        builder.build().register();
        plugin.getLogger().info("Registered MiniPlaceholders expansion: multieconomy ("
                + plugin.getEconomyManager().getCurrencies().size() + " currencies)");
    }

    private static String formatBalance(double balance, int decimalPlaces) {
        if (decimalPlaces == 0) {
            return String.valueOf((long) balance);
        }
        return String.format("%." + decimalPlaces + "f", balance);
    }
}
