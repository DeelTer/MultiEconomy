package ru.deelter.multieconomy;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import ru.deelter.multieconomy.cache.BalanceCache;
import ru.deelter.multieconomy.config.CurrencyConfig;
import ru.deelter.multieconomy.data.Currency;
import ru.deelter.multieconomy.database.EconomyDAO;
import ru.deelter.multieconomy.events.EconomyBalanceChangeEvent;
import ru.deelter.multieconomy.events.EconomyTransferEvent;
import ru.deelter.multieconomy.utils.EconomyVisualiser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class EconomyManager {

	private final Map<String, Currency> currencies = new HashMap<>();
	private final BalanceCache cache;
	private final EconomyDAO dao;

	public EconomyManager(EconomyDAO dao, @NonNull Map<String, CurrencyConfig> currencyConfigs) {
		this.dao = dao;
		this.cache = new BalanceCache(dao);
		for (Map.Entry<String, CurrencyConfig> entry : currencyConfigs.entrySet()) {
			CurrencyConfig cfg = entry.getValue();
			Currency currency = new Currency(
					cfg.getId(),
					cfg.getName(),
					cfg.getIconMiniMessage(),
					cfg.getMaxBalance(),
					cfg.isTransferable(),
					cfg.getTransferFee(),
					cfg.isPrimary(),
					cfg.getDecimalPlaces()
			);
			currencies.put(cfg.getId(), currency);
		}
	}

	public Currency getPrimaryCurrency() {
		return currencies.values().stream().filter(Currency::isPrimary).findFirst()
				.orElse(currencies.get("coins"));
	}

	public double getBalance(UUID holderId, String currencyId) {
		return cache.get(holderId, currencyId);
	}

	public double getBalance(@NonNull OfflinePlayer player, String currencyId) {
		return getBalance(player.getUniqueId(), currencyId);
	}

	public boolean hasAccount(UUID holderId, String currencyId) {
		return cache.hasKey(holderId, currencyId) || dao.exists(holderId, currencyId);
	}

	public void setBalance(UUID holderId, String currencyId, double newBalance) {
		Currency currency = currencies.get(currencyId);
		if (currency == null) throw new IllegalArgumentException("Unknown currency: " + currencyId);
		double oldBalance = getBalance(holderId, currencyId);
		double clamped = Math.clamp(newBalance, 0, currency.getMaxBalance());
		double rounded = currency.round(clamped);

		EconomyBalanceChangeEvent event = new EconomyBalanceChangeEvent(holderId, currencyId, oldBalance, rounded);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return;

		cache.set(holderId, currencyId, event.getNewBalance());

		Player player = Bukkit.getPlayer(holderId);
		if (player != null && player.isOnline()) {
			// Передаём старый баланс и новый
			EconomyVisualiser.addOperation(player, currency, oldBalance, event.getNewBalance());
		}
	}

	public void addBalance(UUID holderId, String currencyId, double amount) {
		double oldBalance = getBalance(holderId, currencyId);
		setBalance(holderId, currencyId, oldBalance + amount);
	}

	public void removeBalance(UUID holderId, String currencyId, double amount) {
		addBalance(holderId, currencyId, -amount);
	}

	public boolean transfer(UUID fromId, UUID toId, String currencyId, double amount) {
		Currency currency = currencies.get(currencyId);
		if (currency == null) return false;
		if (!currency.isTransferable()) return false;
		if (amount <= 0) return false;
		if (fromId.equals(toId)) return false;

		double fee = amount * currency.getTransferFee();
		double totalDeduct = amount + fee;
		double fromBalance = getBalance(fromId, currencyId);
		if (fromBalance < totalDeduct) return false;

		EconomyTransferEvent event = new EconomyTransferEvent(fromId, toId, currencyId, amount, fee);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return false;

		removeBalance(fromId, currencyId, totalDeduct);
		addBalance(toId, currencyId, amount);
		return true;
	}

	public Map<UUID, Double> getTopBalances(String currencyId, int limit) {
		return dao.getTopBalances(currencyId, limit);
	}

	public void saveAll() {
		cache.flushAll();
	}

	public void shutdown() {
		cache.shutdown();
	}

	public void createDefaultAccount(UUID holderId) {
		for (Currency currency : currencies.values()) {
			CurrencyConfig config = MultiEconomy.getInstance().getConfigManager().getCurrencies().get(currency.getId());
			double initial = config.getInitialBalance();
			if (initial > 0 && !hasAccount(holderId, currency.getId())) {
				setBalance(holderId, currency.getId(), initial);
			}
		}
	}
}