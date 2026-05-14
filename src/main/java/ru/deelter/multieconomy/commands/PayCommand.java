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

public class PayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use /pay");
            return true;
        }
        if (!player.hasPermission("multieconomy.pay")) {
            Component msg = MultiEconomy.getInstance().getLang().getMessage("error.no_permission", sender);
            if (msg != null) sender.sendMessage(msg);
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("Usage: /pay <player> <amount>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            Component msg = MultiEconomy.getInstance().getLang().getMessage("error.player_not_found", sender);
            if (msg != null) sender.sendMessage(msg);
            return true;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            Component msg = MultiEconomy.getInstance().getLang().getMessage("error.invalid_amount", sender);
            if (msg != null) sender.sendMessage(msg);
            return true;
        }
        if (amount <= 0) {
            Component msg = MultiEconomy.getInstance().getLang().getMessage("transfer.fail_negative", sender);
            if (msg != null) sender.sendMessage(msg);
            return true;
        }
        Currency primary = MultiEconomy.getInstance().getEconomyManager().getPrimaryCurrency();
        boolean success = MultiEconomy.getInstance().getEconomyManager()
                .transfer(player.getUniqueId(), target.getUniqueId(), primary.getId(), amount);
        Lang lang = MultiEconomy.getInstance().getLang();
        if (success) {
            Component msg = lang.getMessage("transfer.success", sender,
                    "amount", String.valueOf(amount),
                    "currency_icon", primary.getIconMiniMessage(),
                    "currency_name", primary.getName(),
                    "player", target.getName() != null ? target.getName() : "Unknown"
            );
            if (msg != null) sender.sendMessage(msg);
        } else {
            Component msg = lang.getMessage("transfer.fail_insufficient", sender);
            if (msg != null) sender.sendMessage(msg);
        }
        return true;
    }
}