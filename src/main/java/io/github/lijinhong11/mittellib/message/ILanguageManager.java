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
package io.github.lijinhong11.mittellib.message;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The language manager interface
 */
public interface ILanguageManager {
    void sendMessage(@NotNull CommandSender commandSender, String key, MessageReplacement... args);

    void sendMessage(
            @NotNull CommandSender commandSender, String key, ClickEvent clickEvent, MessageReplacement... args);

    void sendMessages(@NotNull CommandSender commandSender, String key, MessageReplacement... args);

    Component getMsgComponent(@Nullable CommandSender commandSender, String key, MessageReplacement... args);

    Component getMsgComponentByLanguage(@Nullable String lang, String key, MessageReplacement... args);

    List<Component> getMsgComponentList(@Nullable CommandSender commandSender, String key, MessageReplacement... args);

    List<Component> getMsgComponentListByLanguage(@Nullable String lang, String key, MessageReplacement... args);

    String getMsg(@Nullable CommandSender sender, String key, MessageReplacement... args);

    List<String> getMsgList(@Nullable CommandSender commandSender, String key, MessageReplacement... args);

    String getMsgByLanguage(@Nullable String lang, String key, MessageReplacement... args);

    List<String> getMsgListByLanguage(@Nullable String lang, String key, MessageReplacement... args);

    @NotNull ItemStack getMessagedItem(
            @NotNull Material material,
            @NotNull String sectionKey,
            @Nullable Player player,
            MessageReplacement... args);

    @NotNull String getParsedLocation(@Nullable CommandSender cs, @NotNull Location loc);

    @NotNull String getParsedBlockLocation(@Nullable CommandSender cs, @NotNull Location loc);

    String getParsedLocation(@Nullable CommandSender cs, double x, double y, double z);

    void reload();

    @NotNull Set<String> getTranslationKeys();

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    class Options {
        private boolean detectPlayerLocale = true;
        private String defaultLanguage = "en-US";
        private String languageSetterKey = "language";
    }
}
