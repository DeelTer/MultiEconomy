package ru.deelter.multieconomy.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

@Getter
@Setter
public class EconomyBalanceChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID holderId;
    private final String currencyId;
    private final double oldBalance;
    private double newBalance;
    private boolean cancelled;

    public EconomyBalanceChangeEvent(UUID holderId, String currencyId, double oldBalance, double newBalance) {
        this.holderId = holderId;
        this.currencyId = currencyId;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}