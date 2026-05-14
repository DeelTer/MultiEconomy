package ru.deelter.multieconomy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import ru.deelter.multieconomy.MultiEconomy;

public class EconomyListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		MultiEconomy.getInstance().getEconomyManager()
				.createDefaultAccount(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// Принудительно сохраняем баланс при выходе
		MultiEconomy.getInstance().getEconomyManager().saveAll();
	}

	@EventHandler
	public void onWorldSave(WorldSaveEvent event) {
		// Периодическое сохранение (дублирует таймер, но не помешает)
		MultiEconomy.getInstance().getEconomyManager().saveAll();
	}
}