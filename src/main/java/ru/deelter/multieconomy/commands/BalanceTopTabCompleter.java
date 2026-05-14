package ru.deelter.multieconomy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;

import java.util.List;
import java.util.stream.Collectors;

public class BalanceTopTabCompleter implements TabCompleter {
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NonNull [] args) {
		if (args.length == 1) {
			// Список валют
			return MultiEconomy.getInstance().getEconomyManager().getCurrencies().values().stream()
					.map(Currency::getId)
					.filter(id -> id.toLowerCase().startsWith(args[0].toLowerCase()))
					.collect(Collectors.toList());
		}
		return List.of();
	}
}