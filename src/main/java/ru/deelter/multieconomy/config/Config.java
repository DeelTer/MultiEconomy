package ru.deelter.multieconomy.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jspecify.annotations.NonNull;
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

    public Config(@NonNull MultiEconomy plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration configuration = plugin.getConfig();

        database = new DatabaseConfig(
                configuration.getString("database.host", "localhost"),
                configuration.getInt("database.port", 3306),
                configuration.getString("database.name", "economy"),
                configuration.getString("database.user", "root"),
                configuration.getString("database.password", ""),
                configuration.getInt("database.pool.maximum-pool-size", 10),
                configuration.getInt("database.pool.minimum-idle", 10),
                configuration.getLong("database.pool.maximum-lifetime", 300000),
                configuration.getLong("database.pool.keepalive-time", 30000),
                configuration.getLong("database.pool.connection-timeout", 5000)
        );

        if (configuration.isConfigurationSection("currencies")) {
            for (String id : configuration.getConfigurationSection("currencies").getKeys(false)) {
                String path = "currencies." + id;
                CurrencyConfig currency = new CurrencyConfig(
                        id,
                        configuration.getString(path + ".name", id),
                        configuration.getString(path + ".icon", "<white>" + id + "</white>"),
                        configuration.getString(path + ".color", null),
                        configuration.getDouble(path + ".initial-balance", 0.0),
                        configuration.getDouble(path + ".max-balance", Double.MAX_VALUE),
                        configuration.getBoolean(path + ".transferable", true),
                        configuration.getDouble(path + ".transfer-fee", 0.0),
                        configuration.getBoolean(path + ".is-primary", false),
                        configuration.getInt(path + ".decimal-places", 2)
                );
                currencies.put(id, currency);
            }
        }

        deathFeePercentage = configuration.getDouble("settings.death-fee-percentage", 0.0);
        saveIntervalSeconds = configuration.getInt("settings.save-interval-seconds", 600);
        soundDropSingle = configuration.getString("sounds.drop-single", "custom.coins.drop.single");
        soundDropMultiply = configuration.getString("sounds.drop-multiply", "custom.coins.drop.multiply");
    }
}