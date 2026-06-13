package ru.deelter.multieconomy.utils;

import io.github.miniplaceholders.api.MiniPlaceholders;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
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
import java.util.*;

@Getter
public class Lang {

	private final MultiEconomy plugin;
	private final Map<String, Map<String, String>> languageMessages = new HashMap<>();
	private final MiniMessage miniMessage = MiniMessage.miniMessage();
	private String defaultLanguage;
	private boolean autoDetect;
	private final boolean miniPlaceholders;

	public Lang(@NotNull MultiEconomy plugin) {
		this.plugin = plugin;
		this.miniPlaceholders = isMiniPlaceholdersAvailable();
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
		saveDefaultLang("uk.yml");

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
		String raw = resolveRawMessage(key, sender instanceof Player player ? player : null);
		if (raw == null) return null;
		if (raw.isEmpty()) return null;
		return parseMessage(raw, sender, resolvers);
	}

	public Component parseMessage(String raw, @Nullable Audience viewer, TagResolver... resolvers) {
		TagResolver combined = TagResolver.resolver(TagResolver.resolver(resolvers));
		if (this.miniPlaceholders)
			combined = TagResolver.resolver(combined, MiniPlaceholders.audienceGlobalPlaceholders());
		return miniMessage.deserialize(raw, viewer == null ? Audience.empty() : viewer, combined);
	}

	// Только строковые плейсхолдеры
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
	public Component getMessage(@NotNull String key, @Nullable CommandSender sender,
	                            @NotNull Map<String, Component> componentPlaceholders,
	                            @NotNull Map<String, String> stringPlaceholders) {
		List<TagResolver> resolvers = new ArrayList<>();
		for (Map.Entry<String, Component> entry : componentPlaceholders.entrySet()) {
			resolvers.add(Placeholder.component(entry.getKey(), entry.getValue()));
		}
		for (Map.Entry<String, String> entry : stringPlaceholders.entrySet()) {
			resolvers.add(Placeholder.unparsed(entry.getKey(), entry.getValue()));
		}
		return getMessage(key, sender, resolvers.toArray(new TagResolver[0]));
	}

	@Nullable
	private String resolveRawMessage(@NotNull String key, @Nullable Player player) {
		String language = resolvePlayerLanguage(player);
		Map<String, String> messages = languageMessages.get(language);
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
		String shortLanguage = locale.getLanguage().toLowerCase();
		if (languageMessages.containsKey(shortLanguage)) {
			return shortLanguage;
		}
		return defaultLanguage;
	}

	private boolean isMiniPlaceholdersAvailable() {
		try {
			Class.forName("io.github.miniplaceholders.api.MiniPlaceholders");
			return true;
		} catch (ClassNotFoundException _) {
			return false;
		}
	}
}