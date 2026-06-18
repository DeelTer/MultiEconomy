package ru.deelter.multieconomy.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;

public class DeathBalanceFeeListener implements Listener {

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		double fee = MultiEconomy.getInstance().getConfigManager().getDeathFeePercentage();
		if (fee <= 0.0) return;

		Player player = event.getPlayer();
		Currency primary = MultiEconomy.getInstance().getEconomyManager().getPrimaryCurrency();
		if (primary == null) return;
		double current = MultiEconomy.getInstance().getEconomyManager().getBalance(player.getUniqueId(), primary.getId());
		double loss = current * fee;
		MultiEconomy.getInstance().getEconomyManager().removeBalance(player.getUniqueId(), primary.getId(), loss);
	}
}