package ru.deelter.multieconomy;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.multieconomy.commands.*;
import ru.deelter.multieconomy.config.Config;
import ru.deelter.multieconomy.database.*;
import ru.deelter.multieconomy.listeners.EconomyListener;
import ru.deelter.multieconomy.utils.Lang;
import ru.deelter.multieconomy.vault.VaultEconomyProvider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public final class MultiEconomy extends JavaPlugin {

    @Getter
    private static MultiEconomy instance;
    private Config configManager;
    private ConnectionPool connectionPool;
    private EconomyDAO economyDAO;
    private EconomyManager economyManager;
    private Lang lang;
    private ScheduledExecutorService scheduler;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configManager = new Config(this);
        lang = new Lang(this);

        DatabaseType dbType = DatabaseType.fromString(getConfig().getString("storage-type", "MYSQL"));
        connectionPool = new ConnectionPool(configManager.getDatabase(), dbType);
        DatabaseInitializer.init(connectionPool);
        economyDAO = new EconomyDAO(connectionPool);
        economyManager = new EconomyManager(economyDAO, configManager.getCurrencies());

        registerCommands();
        Bukkit.getPluginManager().registerEvents(new EconomyListener(), this);

        // Регистрация VaultUnlockedAPI
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Bukkit.getServicesManager().register(
                    net.milkbowl.vault2.economy.Economy.class,
                    new VaultEconomyProvider(),
                    this,
                    org.bukkit.plugin.ServicePriority.High
            );
            getLogger().info("VaultUnlockedAPI hooked.");
        } else {
            getLogger().warning("Vault not found! Economy will work but no external integration.");
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            economyManager.saveAll();
            getLogger().fine("Auto-saved all balances");
        }, configManager.getSaveIntervalSeconds(), configManager.getSaveIntervalSeconds(), TimeUnit.SECONDS);

        getLogger().info("MultiEconomy enabled with " + configManager.getCurrencies().size() + " currencies.");
    }

    private void registerCommands() {
        getCommand("money").setExecutor(new MoneyCommand());
        getCommand("pay").setExecutor(new PayCommand());
        getCommand("eco").setExecutor(new EcoCommand());
        getCommand("balancetop").setExecutor(new BalanceTopCommand());
    }

    @Override
    public void onDisable() {
        if (economyManager != null) {
            economyManager.saveAll();
            economyManager.shutdown();
        }
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        if (connectionPool != null) {
            connectionPool.close();
        }
        getLogger().info("MultiEconomy disabled.");
    }
}