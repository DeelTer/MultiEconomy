package ru.deelter.multieconomy.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EcoTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            // Действия: give, take, set
            return List.of("give", "take", "set").stream()
                    .filter(a -> a.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            // Игроки
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 3) {
            // Сумма (примеры)
            return List.of("100", "500", "1000", "10000");
        }
        if (args.length == 4) {
            // Список валют из конфига
            return MultiEconomy.getInstance().getEconomyManager().getCurrencies().values().stream()
                    .map(Currency::getId)
                    .filter(id -> id.toLowerCase().startsWith(args[3].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}