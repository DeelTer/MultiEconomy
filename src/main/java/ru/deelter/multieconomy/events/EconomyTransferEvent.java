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
public class EconomyTransferEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID fromId;
    private final UUID toId;
    private final String currencyId;
    private final double amount;
    private final double fee;
    private boolean cancelled;

    public EconomyTransferEvent(UUID fromId, UUID toId, String currencyId, double amount, double fee) {
        this.fromId = fromId;
        this.toId = toId;
        this.currencyId = currencyId;
        this.amount = amount;
        this.fee = fee;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}