package ru.deelter.multieconomy.data;

import lombok.Value;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Value
public class Currency {
    String id;
    String name;
    String iconMiniMessage;
    Component icon;
    TextColor color;
    double maxBalance;
    boolean transferable;
    double transferFee;
    boolean primary;
    int decimalPlaces;

    public Currency(String id, String name, String iconMiniMessage, String colorHex, double maxBalance,
                    boolean transferable, double transferFee, boolean primary, int decimalPlaces) {
        this.id = id;
        this.name = name;
        this.iconMiniMessage = iconMiniMessage;
        this.icon = MiniMessage.miniMessage().deserialize(iconMiniMessage);
        this.color = (colorHex != null && !colorHex.isEmpty()) ? TextColor.fromHexString(colorHex) : null;
        this.maxBalance = maxBalance;
        this.transferable = transferable;
        this.transferFee = transferFee;
        this.primary = primary;
        this.decimalPlaces = decimalPlaces;
    }

    public double round(double amount) {
        double factor = Math.pow(10, decimalPlaces);
        return Math.round(amount * factor) / factor;
    }
}