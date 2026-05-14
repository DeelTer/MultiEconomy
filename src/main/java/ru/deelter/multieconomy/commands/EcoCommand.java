package ru.deelter.multieconomy.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;
import ru.deelter.multieconomy.utils.Lang;

public class EcoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("multieconomy.admin")) {
            Component msg = MultiEconomy.getInstance().getLang().getMessage("error-no-permission", sender);
            if (msg != null) sender.sendMessage(msg);
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage("Usage: /eco <give/take/set> <player> <amount> [currency]");
            return true;
        }
        String action = args[0].toLowerCase();
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            Component msg = MultiEconomy.getInstance().getLang().getMessage("error-invalid-amount", sender);
            if (msg != null) sender.sendMessage(msg);
            return true;
        }
        String currencyId = args.length > 3 ? args[3] : MultiEconomy.getInstance().getEconomyManager().getPrimaryCurrency().getId();
        Currency currency = MultiEconomy.getInstance().getEconomyManager().getCurrencies().get(currencyId);
        if (currency == null) {
            Component msg = MultiEconomy.getInstance().getLang().getMessage("error-currency-not-found", sender);
            if (msg != null) sender.sendMessage(msg);
            return true;
        }
        Lang lang = MultiEconomy.getInstance().getLang();
        String playerName = target.getName() != null ? target.getName() : "Unknown";
        switch (action) {
            case "give" -> {
                MultiEconomy.getInstance().getEconomyManager().addBalance(target.getUniqueId(), currencyId, amount);
                Component msg = lang.getMessage("admin-give", sender,
                        "amount", String.valueOf(amount),
                        "currency_icon", currency.getIconMiniMessage(),
                        "currency_name", currency.getName(),
                        "player", playerName
                );
                if (msg != null) sender.sendMessage(msg);
            }
            case "take" -> {
                MultiEconomy.getInstance().getEconomyManager().removeBalance(target.getUniqueId(), currencyId, amount);
                Component msg = lang.getMessage("admin-take", sender,
                        "amount", String.valueOf(amount),
                        "currency_icon", currency.getIconMiniMessage(),
                        "currency_name", currency.getName(),
                        "player", playerName
                );
                if (msg != null) sender.sendMessage(msg);
            }
            case "set" -> {
                MultiEconomy.getInstance().getEconomyManager().setBalance(target.getUniqueId(), currencyId, amount);
                Component msg = lang.getMessage("admin-set", sender,
                        "balance", String.valueOf(amount),
                        "currency_icon", currency.getIconMiniMessage(),
                        "currency_name", currency.getName(),
                        "player", playerName
                );
                if (msg != null) sender.sendMessage(msg);
            }
            default -> sender.sendMessage("Unknown action. Use give/take/set");
        }
        return true;
    }
}