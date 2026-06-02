package ru.deelter.multieconomy.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import ru.deelter.multieconomy.data.EconomyAccount;
import ru.deelter.multieconomy.database.EconomyDAO;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BalanceCache {

	private final EconomyDAO dao;
	private final Cache<CacheKey, Double> cache;
	// Stores unsaved balances; survives Caffeine eviction
	private final Map<CacheKey, Double> pendingSaves = new ConcurrentHashMap<>();

	public BalanceCache(EconomyDAO dao) {
		this.dao = dao;
		this.cache = Caffeine.newBuilder()
				.expireAfterAccess(30, TimeUnit.MINUTES)
				.maximumSize(10_000)
				.removalListener((key, value, cause) -> {
					// pendingSaves already holds the value if dirty — nothing extra needed
				})
				.build();
	}

	public double get(UUID holderId, String currencyId) {
		CacheKey key = new CacheKey(holderId, currencyId);
		Double value = cache.getIfPresent(key);
		if (value != null) {
			return value;
		}
		Optional<EconomyAccount> account = dao.load(holderId, currencyId);
		double balance = account.map(EconomyAccount::getBalance).orElse(0.0);
		cache.put(key, balance);
		return balance;
	}

	public void set(UUID holderId, String currencyId, double balance) {
		CacheKey key = new CacheKey(holderId, currencyId);
		cache.put(key, balance);
		pendingSaves.put(key, balance);
	}

	public void invalidate(UUID holderId, String currencyId) {
		CacheKey key = new CacheKey(holderId, currencyId);
		cache.invalidate(key);
		pendingSaves.remove(key);
	}

	public void flushDirty() {
		if (pendingSaves.isEmpty()) return;
		Map<UUID, Map<String, Double>> updates = new HashMap<>();
		Set<CacheKey> toFlush = new HashSet<>(pendingSaves.keySet());
		for (CacheKey key : toFlush) {
			Double balance = pendingSaves.get(key);
			if (balance != null) {
				updates.computeIfAbsent(key.holderId(), k -> new HashMap<>())
						.put(key.currencyId(), balance);
			}
		}
		if (!updates.isEmpty()) {
			dao.batchSaveOrUpdate(updates);
			pendingSaves.keySet().removeAll(toFlush);
		}
	}

	public void flushAll() {
		flushDirty();
	}

	public void shutdown() {
		flushAll();
	}

	public boolean hasKey(UUID holderId, String currencyId) {
		return cache.asMap().containsKey(new CacheKey(holderId, currencyId));
	}

	public void invalidatePlayer(UUID holderId) {
		cache.asMap().keySet().stream()
				.filter(k -> k.holderId().equals(holderId))
				.forEach(k -> {
					cache.invalidate(k);
					pendingSaves.remove(k);
				});
	}

	public record CacheKey(UUID holderId, String currencyId) {
	}
}
