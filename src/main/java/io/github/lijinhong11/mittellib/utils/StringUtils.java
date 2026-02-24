package io.github.lijinhong11.mittellib.utils;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.message.LanguageManager;
import io.github.miniplaceholders.api.MiniPlaceholders;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringUtils {
    private static final PlainTextComponentSerializer COMPONENT_PLAIN = PlainTextComponentSerializer.plainText();

    public static String getBooleanStatus(@Nullable CommandSender cs, boolean b) {
        LanguageManager lm = MittelLib.getInstance().getLanguageManager();
        return b ? lm.getMsg(cs, "common.enabled") : lm.getMsg(cs, "common.disabled");
    }

    public static String parsePlaceholders(@NotNull String text) {
        return parsePlaceholders(null, text);
    }

    public static String parsePlaceholders(@Nullable CommandSender cs, @NotNull String text) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(cs instanceof Player p ? p : null, text);
        }

        Component result = Component.text(text);

        if (Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            String plain = COMPONENT_PLAIN.serialize(result);
            return COMPONENT_PLAIN.serialize(
                    MiniMessage.miniMessage().deserialize(plain, MiniPlaceholders.globalPlaceholders()));
        }

        return text;
    }
}
