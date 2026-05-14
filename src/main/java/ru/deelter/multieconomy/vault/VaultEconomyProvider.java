package ru.deelter.multieconomy.vault;

import net.milkbowl.vault2.economy.AccountPermission;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

public class VaultEconomyProvider implements Economy {

	private final Currency primaryCurrency;

	public VaultEconomyProvider() {
		this.primaryCurrency = MultiEconomy.getInstance().getEconomyManager().getPrimaryCurrency();
	}

	// ==================== Базовые методы ====================
	@Override
	public boolean isEnabled() {
		return MultiEconomy.getInstance().isEnabled();
	}

	@Override
	public @NotNull String getName() {
		return "MultiEconomy";
	}

	@Override
	public boolean hasSharedAccountSupport() {
		return false;
	}

	@Override
	public boolean hasMultiCurrencySupport() {
		return true;
	}

	// ==================== Валюты ====================
	@Override
	public int fractionalDigits(@NotNull String pluginName) {
		return primaryCurrency.getDecimalPlaces();
	}

	@Override
	public int fractionalDigits(@NotNull String pluginName, @NotNull String currency) {
		Currency cur = MultiEconomy.getInstance().getEconomyManager().getCurrencies().get(currency);
		return cur != null ? cur.getDecimalPlaces() : fractionalDigits(pluginName);
	}

	@Override
	public @NotNull String format(@NotNull BigDecimal amount) {
		return "";
	}

	@Override
	public @NotNull String format(@NotNull String pluginName, @NotNull BigDecimal amount) {
		return format(pluginName, amount, primaryCurrency.getId());
	}

	@Override
	public @NotNull String format(@NotNull BigDecimal amount, @NotNull String currency) {
		return "";
	}

	@Override
	public @NotNull String format(@NotNull String pluginName, @NotNull BigDecimal amount, @NotNull String currency) {
		Currency cur = MultiEconomy.getInstance().getEconomyManager().getCurrencies().get(currency);
		if (cur == null) cur = primaryCurrency;
		if (cur.getDecimalPlaces() == 0) {
			return String.valueOf(amount.longValue());
		}
		String pattern = "#." + "#".repeat(cur.getDecimalPlaces());
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(amount);
	}

	@Override
	public boolean hasCurrency(@NotNull String currency) {
		return MultiEconomy.getInstance().getEconomyManager().getCurrencies().containsKey(currency);
	}

	@Override
	public @NotNull String getDefaultCurrency(@NotNull String pluginName) {
		return primaryCurrency.getId();
	}

	@Override
	public @NotNull String defaultCurrencyNamePlural(@NotNull String pluginName) {
		return primaryCurrency.getName();
	}

	@Override
	public @NotNull String defaultCurrencyNameSingular(@NotNull String pluginName) {
		return primaryCurrency.getName();
	}

	@Override
	public @NotNull Collection<String> currencies() {
		return MultiEconomy.getInstance().getEconomyManager().getCurrencies().keySet();
	}

	@Override
	public boolean createAccount(@NotNull UUID accountID, @NotNull String name) {
		return false;
	}

	// ==================== Аккаунты ====================
	@Override
	public boolean createAccount(@NotNull UUID accountID, @NotNull String name, boolean player) {
		return true;
	}

	@Override
	public boolean createAccount(@NotNull UUID accountID, @NotNull String name, @NotNull String worldName) {
		return false;
	}

	@Override
	public boolean createAccount(@NotNull UUID accountID, @NotNull String name, @NotNull String worldName, boolean player) {
		return true;
	}

	@Override
	public @NotNull Map<UUID, String> getUUIDNameMap() {
		return Collections.emptyMap();
	}

	@Override
	public Optional<String> getAccountName(@NotNull UUID accountID) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(accountID);
		return Optional.ofNullable(player.getName());
	}

	@Override
	public boolean hasAccount(@NotNull UUID accountID) {
		return true;
	}

	@Override
	public boolean hasAccount(@NotNull UUID accountID, @NotNull String worldName) {
		return true;
	}

	@Override
	public boolean renameAccount(@NotNull UUID accountID, @NotNull String name) {
		return renameAccount(getName(), accountID, name);
	}

	@Override
	public boolean renameAccount(@NotNull String plugin, @NotNull UUID accountID, @NotNull String name) {
		return true;
	}

	@Override
	public boolean deleteAccount(@NotNull String plugin, @NotNull UUID accountID) {
		return false;
	}

	// ==================== Балансы (современные методы) ====================
	@Override
	public boolean accountSupportsCurrency(@NotNull String plugin, @NotNull UUID accountID, @NotNull String currency) {
		return hasCurrency(currency);
	}

	@Override
	public boolean accountSupportsCurrency(@NotNull String plugin, @NotNull UUID accountID, @NotNull String currency, @NotNull String world) {
		return hasCurrency(currency);
	}

	@Override
	public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID) {
		return null;
	}

	@Override
	public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world) {
		return null;
	}

	@Override
	public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world, @NotNull String currency) {
		return null;
	}

	@Override
	public @NotNull BigDecimal balance(@NotNull String pluginName, @NotNull UUID accountID) {
		return BigDecimal.valueOf(MultiEconomy.getInstance().getEconomyManager().getBalance(accountID, primaryCurrency.getId()));
	}

	@Override
	public @NotNull BigDecimal balance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world) {
		return balance(pluginName, accountID);
	}

	@Override
	public @NotNull BigDecimal balance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world, @NotNull String currency) {
		Currency cur = MultiEconomy.getInstance().getEconomyManager().getCurrencies().get(currency);
		if (cur == null) return BigDecimal.ZERO;
		return BigDecimal.valueOf(MultiEconomy.getInstance().getEconomyManager().getBalance(accountID, cur.getId()));
	}

	@Override
	public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
		return has(pluginName, accountID, null, amount);
	}

	@Override
	public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
		return has(pluginName, accountID, worldName, primaryCurrency.getId(), amount);
	}

	@Override
	public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
		Currency cur = MultiEconomy.getInstance().getEconomyManager().getCurrencies().get(currency);
		if (cur == null) return false;
		double balance = MultiEconomy.getInstance().getEconomyManager().getBalance(accountID, cur.getId());
		return balance >= amount.doubleValue();
	}

	@Override
	public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
		return withdraw(pluginName, accountID, null, amount);
	}

	@Override
	public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
		return withdraw(pluginName, accountID, worldName, primaryCurrency.getId(), amount);
	}

	@Override
	public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			return new EconomyResponse(null, null, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative amount");
		}
		Currency cur = MultiEconomy.getInstance().getEconomyManager().getCurrencies().get(currency);
		if (cur == null) {
			return new EconomyResponse(null, null, EconomyResponse.ResponseType.FAILURE, "Unknown currency");
		}
		double current = MultiEconomy.getInstance().getEconomyManager().getBalance(accountID, cur.getId());
		if (current < amount.doubleValue()) {
			return new EconomyResponse(null, BigDecimal.valueOf(current), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
		}
		MultiEconomy.getInstance().getEconomyManager().removeBalance(accountID, cur.getId(), amount.doubleValue());
		double newBalance = MultiEconomy.getInstance().getEconomyManager().getBalance(accountID, cur.getId());
		return new EconomyResponse(amount, BigDecimal.valueOf(newBalance), EconomyResponse.ResponseType.SUCCESS, null);
	}

	@Override
	public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
		return deposit(pluginName, accountID, null, amount);
	}

	@Override
	public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
		return deposit(pluginName, accountID, worldName, primaryCurrency.getId(), amount);
	}

	@Override
	public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			return new EconomyResponse(null, null, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative amount");
		}
		Currency cur = MultiEconomy.getInstance().getEconomyManager().getCurrencies().get(currency);
		if (cur == null) {
			return new EconomyResponse(null, null, EconomyResponse.ResponseType.FAILURE, "Unknown currency");
		}
		MultiEconomy.getInstance().getEconomyManager().addBalance(accountID, cur.getId(), amount.doubleValue());
		double newBalance = MultiEconomy.getInstance().getEconomyManager().getBalance(accountID, cur.getId());
		return new EconomyResponse(amount, BigDecimal.valueOf(newBalance), EconomyResponse.ResponseType.SUCCESS, null);
	}

	// ==================== Shared accounts (не поддерживаем) ====================
	@Override
	public boolean createSharedAccount(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String name, @NotNull UUID owner) {
		return false;
	}

	@Override
	public boolean isAccountOwner(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
		return false;
	}

	@Override
	public boolean setOwner(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
		return false;
	}

	@Override
	public boolean isAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
		return false;
	}

	@Override
	public boolean addAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
		return false;
	}

	@Override
	public boolean addAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission... initialPermissions) {
		return false;
	}

	@Override
	public boolean removeAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
		return false;
	}

	@Override
	public boolean hasAccountPermission(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission permission) {
		return false;
	}

	@Override
	public boolean updateAccountPermission(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission permission, boolean value) {
		return false;
	}
}