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

public class MoneyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        Lang lang = MultiEconomy.getInstance().getLang();
        Currency primary = MultiEconomy.getInstance().getEconomyManager().getPrimaryCurrency();
        OfflinePlayer target;

        if (args.length >= 1) {
            target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                Component msg = lang.getMessage("error.player_not_found", sender);
                if (msg != null) sender.sendMessage(msg);
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can check their own balance. Use /money <player>");
                return true;
            }
            target = (Player) sender;
        }

        double balance = MultiEconomy.getInstance().getEconomyManager()
                .getBalance(target.getUniqueId(), primary.getId());

        String key = target.equals(sender) ? "money.show" : "money.other";
        Component msg = lang.getMessage(key, sender,
                "player", target.getName() != null ? target.getName() : "Unknown",
                "balance", String.valueOf(balance),
                "currency_icon", primary.getIconMiniMessage(),
                "currency_name", primary.getName()
        );
        if (msg != null) sender.sendMessage(msg);
        return true;
    }
}