package ru.deelter.multieconomy.data;

import lombok.Value;
import net.kyori.adventure.text.format.TextColor;

@Value
public class Currency {
    String id;
    String name;
    String nameMiniMessage;
    String iconMiniMessage;
    TextColor color;
    double maxBalance;
    boolean transferable;
    double transferFee;
    boolean primary;
    int decimalPlaces;

    public Currency(String id, String name, String nameMiniMessage, String iconMiniMessage, String colorHex, double maxBalance,
                    boolean transferable, double transferFee, boolean primary, int decimalPlaces) {
        this.id = id;
        this.name = name;
        this.nameMiniMessage = nameMiniMessage;
        this.iconMiniMessage = iconMiniMessage;
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