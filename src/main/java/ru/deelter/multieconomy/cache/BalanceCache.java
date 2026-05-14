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
	private final Set<CacheKey> dirtyKeys = ConcurrentHashMap.newKeySet();

	public BalanceCache(EconomyDAO dao) {
		this.dao = dao;
		this.cache = Caffeine.newBuilder()
				.expireAfterAccess(30, TimeUnit.MINUTES)
				.maximumSize(10_000)
				.removalListener((key, value, cause) -> {
					if (cause.wasEvicted() && value != null) {
						dirtyKeys.add((CacheKey) key);
					}
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
		dirtyKeys.add(key);
	}

	public void invalidate(UUID holderId, String currencyId) {
		CacheKey key = new CacheKey(holderId, currencyId);
		cache.invalidate(key);
		dirtyKeys.remove(key);
	}

	public void flushDirty() {
		if (dirtyKeys.isEmpty()) return;
		Map<UUID, Map<String, Double>> updates = new HashMap<>();
		for (CacheKey key : dirtyKeys) {
			Double balance = cache.getIfPresent(key);
			if (balance != null) {
				updates.computeIfAbsent(key.holderId(), k -> new HashMap<>())
						.put(key.currencyId(), balance);
			}
		}
		if (!updates.isEmpty()) {
			dao.batchSaveOrUpdate(updates);
			// Удаляем только те ключи, которые были реально сохранены
			Set<CacheKey> savedKeys = new HashSet<>();
			for (Map.Entry<UUID, Map<String, Double>> entry : updates.entrySet()) {
				UUID uuid = entry.getKey();
				for (String curId : entry.getValue().keySet()) {
					savedKeys.add(new CacheKey(uuid, curId));
				}
			}
			dirtyKeys.removeAll(savedKeys);
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

	public record CacheKey(UUID holderId, String currencyId) {
	}
}