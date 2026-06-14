package ru.deelter.multieconomy.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;
import ru.deelter.multieconomy.utils.EconomyUtils;
import ru.deelter.multieconomy.utils.Lang;

import java.util.Map;

public class MoneyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Lang lang = MultiEconomy.getInstance().getLang();
        Currency primaryCurrency = MultiEconomy.getInstance().getEconomyManager().getPrimaryCurrency();
        OfflinePlayer target;

        if (args.length >= 1) {
            target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                Component message = lang.getMessage("error-player-not-found", sender);
                if (message != null) sender.sendMessage(message);
                return true;
            }
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can check their own balance. Use /money <player>");
                return true;
            }
            target = player;
        }

        double balance = MultiEconomy.getInstance().getEconomyManager()
                .getBalance(target.getUniqueId(), primaryCurrency.getId());

        String key = target.equals(sender) ? "money-show" : "money-other";

        Component targetName
            = target instanceof Player targetPlayer
            ? targetPlayer.displayName().hoverEvent(HoverEvent.showEntity(targetPlayer.getType().getKey(), targetPlayer.getUniqueId()))
            : Component.text(target.getName() == null ? target.getUniqueId().toString() : target.getName());

        Component message = lang.getMessage(key, sender,
                Map.of(
                    "currency_name", lang.parseMessage(primaryCurrency.getNameMiniMessage(), sender),
                    "currency_icon", lang.parseMessage(primaryCurrency.getIconMiniMessage(), sender),
                    "player", targetName
                ),
                Map.of(
                    "currency_id", primaryCurrency.getId(),
                    "balance", EconomyUtils.formatAmount(balance, primaryCurrency.getDecimalPlaces())
                )
        );
        if (message != null) sender.sendMessage(message);
        return true;
    }
}