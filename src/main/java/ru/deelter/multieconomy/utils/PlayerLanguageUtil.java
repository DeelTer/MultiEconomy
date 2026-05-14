package ru.deelter.multieconomy.utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Locale;

public final class PlayerLanguageUtil {

    private PlayerLanguageUtil() {}

    @NotNull
    public static Locale getLocale(@NotNull Player player) {
        return player.locale();
    }
}