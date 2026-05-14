package ru.deelter.multieconomy.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EconomyVisualiser {

    private static final Map<Player, VisualOperation[]> OPERATIONS = new HashMap<>();
    private static boolean taskStarted = false;

    public static void addOperation(Player player, Currency currency, double newBalance) {
        if (!taskStarted) startTask();
        VisualOperation[] operations = OPERATIONS.computeIfAbsent(player, k -> new VisualOperation[MultiEconomy.getInstance().getEconomyManager().getCurrencies().size()]);
        int index = getCurrencyIndex(currency);
        VisualOperation operation = operations[index];

        if (operation == null) {
            double current = MultiEconomy.getInstance().getEconomyManager().getBalance(player.getUniqueId(), currency.getId());
            operation = new VisualOperation(currency, current, newBalance, currency.getMaxBalance());
            operations[index] = operation;
        } else {
            operation.setNewBalance(newBalance);
        }
    }

    private static int getCurrencyIndex(Currency currency) {
        int i = 0;
        for (Currency c : MultiEconomy.getInstance().getEconomyManager().getCurrencies().values()) {
            if (c.getId().equals(currency.getId())) return i;
            i++;
        }
        return 0;
    }

    private static void startTask() {
        taskStarted = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimerAsynchronously(MultiEconomy.getInstance(), 1L, 1L);
    }

    private static void tick() {
        OPERATIONS.entrySet().removeIf(entry -> {
            Player player = entry.getKey();
            if (player == null || !player.isOnline()) {
                return true;
            }

            boolean shouldPlaySound = false;
            VisualOperation[] operations = entry.getValue();

            for (VisualOperation operation : operations) {
                if (operation == null) continue;

                long lastDisplayValue = Math.round(operation.getCurrentBalance());
                operation.update();

                if (Math.round(operation.getCurrentBalance()) != lastDisplayValue) {
                    shouldPlaySound = true;
                }
            }
            if (shouldPlaySound) {
                player.playSound(EconomyUtils.getDropSingleSound());
            }

            Component actionBar = Component.join(JoinConfiguration.builder().separator(Component.text(" ")).build(),
                    Arrays.stream(operations)
                            .filter(Objects::nonNull)
                            .map(VisualOperation::toComponent)
                            .toList());
            player.sendActionBar(actionBar);

            boolean uncompleted = false;
            for (int i = 0; i < operations.length; i++) {
                VisualOperation operation = operations[i];
                if (operation == null) continue;
                if (!operation.isCompleted()) {
                    uncompleted = true;
                    continue;
                }
                player.playSound(EconomyUtils.getDropMultiplySound());
                operations[i] = null;
            }
            return !uncompleted;
        });
    }
}