package ru.deelter.multieconomy.utils;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.deelter.multieconomy.MultiEconomy;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
public class Lang {

    private final MultiEconomy plugin;
    private final Map<String, Map<String, String>> languageMessages = new HashMap<>();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private String defaultLanguage;
    private boolean autoDetect;

    public Lang(@NotNull MultiEconomy plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        languageMessages.clear();
        defaultLanguage = plugin.getConfig().getString("language.default", "en");
        autoDetect = plugin.getConfig().getBoolean("language.auto-detect", true);

        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        saveDefaultLang("en.yml");
        saveDefaultLang("ru.yml");

        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String langCode = file.getName().replace(".yml", "");
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                Map<String, String> messages = new HashMap<>();
                if (cfg.isConfigurationSection("messages")) {
                    for (String key : cfg.getConfigurationSection("messages").getKeys(false)) {
                        messages.put(key, cfg.getString("messages." + key));
                    }
                }
                languageMessages.put(langCode, messages);
            }
        }
    }

    private void saveDefaultLang(@NotNull String fileName) {
        File target = new File(plugin.getDataFolder(), "lang/" + fileName);
        if (!target.exists()) {
            plugin.saveResource("lang/" + fileName, false);
        }
    }

    @Nullable
    public Component getMessage(@NotNull String key, @Nullable CommandSender sender) {
        return getMessage(key, sender, TagResolver.empty());
    }

    @Nullable
    public Component getMessage(@NotNull String key, @Nullable CommandSender sender, TagResolver... resolvers) {
        Player player = (sender instanceof Player) ? (Player) sender : null;
        String raw = resolveRawMessage(key, player);
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        TagResolver combined = TagResolver.resolver(resolvers);
        return miniMessage.deserialize(raw, combined);
    }

    // Метод для одного плейсхолдера
    @Nullable
    public Component getMessage(@NotNull String key, @Nullable CommandSender sender, String placeholderKey, String value) {
        return getMessage(key, sender, Placeholder.unparsed(placeholderKey, value));
    }

    // Метод для произвольного числа плейсхолдеров (чередование ключ, значение)
    @Nullable
    public Component getMessage(@NotNull String key, @Nullable CommandSender sender, @NotNull String... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be in key-value pairs");
        }
        TagResolver[] resolvers = new TagResolver[placeholders.length / 2];
        for (int i = 0; i < placeholders.length; i += 2) {
            resolvers[i / 2] = Placeholder.unparsed(placeholders[i], placeholders[i + 1]);
        }
        return getMessage(key, sender, resolvers);
    }

    @Nullable
    private String resolveRawMessage(@NotNull String key, @Nullable Player player) {
        String lang = resolvePlayerLanguage(player);
        Map<String, String> messages = languageMessages.get(lang);
        if (messages != null && messages.containsKey(key)) {
            return messages.get(key);
        }
        Map<String, String> defaultMessages = languageMessages.get(defaultLanguage);
        if (defaultMessages != null && defaultMessages.containsKey(key)) {
            return defaultMessages.get(key);
        }
        return key;
    }

    @NotNull
    private String resolvePlayerLanguage(@Nullable Player player) {
        if (!autoDetect || player == null) {
            return defaultLanguage;
        }
        Locale locale = PlayerLanguageUtil.getLocale(player);
        String shortLang = locale.getLanguage().toLowerCase();
        if (languageMessages.containsKey(shortLang)) {
            return shortLang;
        }
        return defaultLanguage;
    }
}