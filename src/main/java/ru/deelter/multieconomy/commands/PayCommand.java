package ru.deelter.multieconomy.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;
import ru.deelter.multieconomy.utils.Lang;

import java.util.Map;

public class PayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use /pay");
            return true;
        }

        Lang lang = MultiEconomy.getInstance().getLang();

        if (!player.hasPermission("multieconomy.pay")) {
            Component message = lang.getMessage("error-no-permission", sender);
            if (message != null) sender.sendMessage(message);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /pay <player> <amount>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            Component message = lang.getMessage("error-player-not-found", sender);
            if (message != null) sender.sendMessage(message);
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException exception) {
            Component message = lang.getMessage("error-invalid-amount", sender);
            if (message != null) sender.sendMessage(message);
            return true;
        }

        if (amount <= 0) {
            Component message = lang.getMessage("transfer-fail-negative", sender);
            if (message != null) sender.sendMessage(message);
            return true;
        }

        Currency primaryCurrency = MultiEconomy.getInstance().getEconomyManager().getPrimaryCurrency();
        boolean success = MultiEconomy.getInstance().getEconomyManager()
                .transfer(player.getUniqueId(), target.getUniqueId(), primaryCurrency.getId(), amount);

        String targetName = target.getName() != null ? target.getName() : "Unknown";

        if (success) {
            Component message = lang.getMessage("transfer-success", sender,
                    Map.of("currency_icon", primaryCurrency.getIcon()),
                    Map.of(
                            "amount", String.valueOf(amount),
                            "currency_name", primaryCurrency.getName(),
                            "player", targetName
                    )
            );
            if (message != null) sender.sendMessage(message);
        } else {
            Component message = lang.getMessage("transfer-fail-insufficient", sender);
            if (message != null) sender.sendMessage(message);
        }
        return true;
    }
}