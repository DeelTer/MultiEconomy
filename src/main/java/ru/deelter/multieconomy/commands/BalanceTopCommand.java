package ru.deelter.multieconomy.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;
import ru.deelter.multieconomy.utils.Lang;

import java.util.Map;
import java.util.UUID;

public class BalanceTopCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		Lang lang = MultiEconomy.getInstance().getLang();
		String currencyId = args.length > 0 ? args[0] : MultiEconomy.getInstance().getEconomyManager().getPrimaryCurrency().getId();
		Currency currency = MultiEconomy.getInstance().getEconomyManager().getCurrencies().get(currencyId);
		if (currency == null) {
			sender.sendMessage(Component.text("Currency not found", NamedTextColor.RED));
			return true;
		}
		Map<UUID, Double> top = MultiEconomy.getInstance().getEconomyManager().getTopBalances(currencyId, 10);
		if (top.isEmpty()) {
			sender.sendMessage(Component.text("No data", NamedTextColor.YELLOW));
			return true;
		}
		sender.sendMessage(Component.text("=== Top " + lang.parseMessage(currency.getNameMiniMessage(), sender) + " ===", NamedTextColor.GOLD));
		int rank = 1;
		for (Map.Entry<UUID, Double> entry : top.entrySet()) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
			String name = player.getName() != null ? player.getName() : "Unknown";
			Component line = Component.text(rank + ". " + name + ": ", NamedTextColor.WHITE)
					.append(lang.parseMessage(currency.getIconMiniMessage(), sender, Placeholder.unparsed("amount", String.valueOf(entry.getValue()))))
					.append(Component.text(" " + entry.getValue(), NamedTextColor.YELLOW));
			sender.sendMessage(line);
			rank++;
		}
		return true;
	}
}