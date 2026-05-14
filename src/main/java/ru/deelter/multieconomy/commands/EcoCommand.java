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
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		Lang language = MultiEconomy.getInstance().getLang();

		if (!sender.hasPermission("multieconomy.admin")) {
			Component message = language.getMessage("error-no-permission", sender);
			if (message != null) sender.sendMessage(message);
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
		} catch (NumberFormatException exception) {
			Component message = language.getMessage("error-invalid-amount", sender);
			if (message != null) sender.sendMessage(message);
			return true;
		}

		String currencyId = args.length > 3 ? args[3] : MultiEconomy.getInstance().getEconomyManager().getPrimaryCurrency().getId();
		Currency currency = MultiEconomy.getInstance().getEconomyManager().getCurrencies().get(currencyId);

		if (currency == null) {
			Component message = language.getMessage("error-currency-not-found", sender);
			if (message != null) sender.sendMessage(message);
			return true;
		}

		String playerName = target.getName() != null ? target.getName() : "Unknown";

		switch (action) {
			case "give" -> {
				MultiEconomy.getInstance().getEconomyManager().addBalance(target.getUniqueId(), currencyId, amount);
				Component message = language.getMessage("admin-give", sender,
						"amount", String.valueOf(amount),
						"currency_icon", currency.getIconMiniMessage(),
						"currency_name", currency.getName(),
						"player", playerName
				);
				if (message != null) sender.sendMessage(message);
			}
			case "take" -> {
				MultiEconomy.getInstance().getEconomyManager().removeBalance(target.getUniqueId(), currencyId, amount);
				Component message = language.getMessage("admin-take", sender,
						"amount", String.valueOf(amount),
						"currency_icon", currency.getIconMiniMessage(),
						"currency_name", currency.getName(),
						"player", playerName
				);
				if (message != null) sender.sendMessage(message);
			}
			case "set" -> {
				MultiEconomy.getInstance().getEconomyManager().setBalance(target.getUniqueId(), currencyId, amount);
				Component message = language.getMessage("admin-set", sender,
						"balance", String.valueOf(amount),
						"currency_icon", currency.getIconMiniMessage(),
						"currency_name", currency.getName(),
						"player", playerName
				);
				if (message != null) sender.sendMessage(message);
			}
			default -> sender.sendMessage("Unknown action. Use give/take/set");
		}
		return true;
	}
}