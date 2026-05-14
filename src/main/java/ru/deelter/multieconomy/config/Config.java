package ru.deelter.multieconomy.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import ru.deelter.multieconomy.MultiEconomy;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Config {

    private final DatabaseConfig database;
    private final Map<String, CurrencyConfig> currencies = new HashMap<>();
    private final double deathFeePercentage;
    private final int saveIntervalSeconds;
    private final String soundDropSingle;
    private final String soundDropMultiply;

    public Config(MultiEconomy plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration cfg = plugin.getConfig();

        // Database
        database = new DatabaseConfig(
                cfg.getString("database.host", "localhost"),
                cfg.getInt("database.port", 3306),
                cfg.getString("database.name", "economy"),
                cfg.getString("database.user", "root"),
                cfg.getString("database.password", ""),
                cfg.getInt("database.pool.maximum-pool-size", 10),
                cfg.getInt("database.pool.minimum-idle", 10),
                cfg.getLong("database.pool.maximum-lifetime", 300000),
                cfg.getLong("database.pool.keepalive-time", 30000),
                cfg.getLong("database.pool.connection-timeout", 5000)
        );

        // Currencies
        if (cfg.isConfigurationSection("currencies")) {
            for (String id : cfg.getConfigurationSection("currencies").getKeys(false)) {
                String path = "currencies." + id;
                CurrencyConfig cur = new CurrencyConfig(
                        id,
                        cfg.getString(path + ".name", id),
                        cfg.getString(path + ".icon", "<white>" + id + "</white>"),
                        cfg.getDouble(path + ".initial-balance", 0.0),
                        cfg.getDouble(path + ".max-balance", Double.MAX_VALUE),
                        cfg.getBoolean(path + ".transferable", true),
                        cfg.getDouble(path + ".transfer-fee", 0.0),
                        cfg.getBoolean(path + ".is-primary", false),
                        cfg.getInt(path + ".decimal-places", 2)
                );
                currencies.put(id, cur);
            }
        }

        deathFeePercentage = cfg.getDouble("settings.death-fee-percentage", 0.0);
        saveIntervalSeconds = cfg.getInt("settings.save-interval-seconds", 600);
        soundDropSingle = cfg.getString("sounds.drop-single", "custom.coins.drop.single");
        soundDropMultiply = cfg.getString("sounds.drop-multiply", "custom.coins.drop.multiply");
    }
}