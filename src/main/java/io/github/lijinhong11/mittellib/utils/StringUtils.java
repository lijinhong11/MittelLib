/*
 * MittelLib
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.mittellib.utils;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.message.SyncLanguageManager;
import io.github.miniplaceholders.api.MiniPlaceholders;
import java.sql.Date;
import java.time.Instant;
import java.util.Locale;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class StringUtils {
    private static final PlainTextComponentSerializer COMPONENT_PLAIN = PlainTextComponentSerializer.plainText();

    public static @NotNull String toBooleanStatus(@Nullable CommandSender cs, boolean b) {
        SyncLanguageManager lm = MittelLib.getInstance().getLanguageManager();
        return b ? lm.getMsg(cs, "common.enabled") : lm.getMsg(cs, "common.disabled");
    }

    public static @NotNull String parsePlaceholders(@NotNull String text) {
        return parsePlaceholders(null, text);
    }

    public static @NotNull String parsePlaceholders(@Nullable CommandSender cs, @NotNull String text) {
        if (cs instanceof Player a) {
            text = text.replace("%player%", a.getName());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(cs instanceof Player p ? p : null, text);
        }

        Component result = Component.text(text);

        if (Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            String plain = COMPONENT_PLAIN.serialize(result);
            if (cs != null) {
                return COMPONENT_PLAIN.serialize(MiniMessage.miniMessage()
                        .deserialize(plain, cs, MiniPlaceholders.audienceGlobalPlaceholders()));
            } else {
                return COMPONENT_PLAIN.serialize(
                        MiniMessage.miniMessage().deserialize(plain, MiniPlaceholders.globalPlaceholders()));
            }
        }

        return text;
    }

    public static @NotNull String convertToRightLangCode(@Nullable String lang) {
        if (lang == null || lang.isBlank()) return "en-US";
        String normalized = lang.replace('_', '-');
        String[] split = normalized.split("-");
        if (split.length == 1) return normalized;

        return split[0] + "-" + split[1].toUpperCase(Locale.ROOT);
    }

    public static @NotNull String formatCountdown(int totalSeconds) {
        int days = totalSeconds / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return MittelLib.getInstance()
                .getCountdownFormat()
                .replace("%days%", String.valueOf(days))
                .replace("%hours%", String.valueOf(hours))
                .replace("%minutes%", String.valueOf(minutes))
                .replace("%seconds%", String.valueOf(seconds));
    }

    public static @NotNull String formatDateOfNow() {
        return DateFormatUtils.format(
                Date.from(Instant.now()), MittelLib.getInstance().getDateFormat());
    }

    public static @NotNull String formatDate(long epochmilli) {
        return DateFormatUtils.format(
                Date.from(Instant.ofEpochMilli(epochmilli)),
                MittelLib.getInstance().getDateFormat());
    }
}
