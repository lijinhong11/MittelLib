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

import io.github.lijinhong11.mittellib.utils.StringUtils;
import java.io.File;
import java.util.Objects;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class to manage language files and messages from local folders.
 */
public final class LocalLanguageManager extends AbstractLanguageManager {
    @Setter
    private Options options;

    public LocalLanguageManager(@NotNull Plugin plugin) {
        this(plugin, new Options());
    }

    public LocalLanguageManager(@NotNull Plugin plugin, @NotNull Options options) {
        super(plugin, options.getDefaultLanguage());
        this.options = options;
        this.detectPlayerLocale = options.isDetectPlayerLocale();
        loadLanguages();
    }

    @Override
    protected void loadLanguages() {
        File pluginFolder = plugin.getDataFolder();
        File languageFolder = new File(pluginFolder, "language");

        defaultConfiguration =
                YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "language/en-US.yml"));

        File[] languageFiles = languageFolder.listFiles(f -> f.getName().endsWith(".yml"));
        if (languageFiles != null) {
            for (File languageFile : languageFiles) {
                String language = StringUtils.convertToRightLangCode(
                        languageFile.getName().replaceAll(".yml", ""));
                configurations.put(language, YamlConfiguration.loadConfiguration(languageFile));
            }
        }
    }

    @Override
    protected @NotNull Configuration getConfiguration(@Nullable CommandSender p) {
        if (!detectPlayerLocale || !(p instanceof Player pl)) {
            String lang = plugin.getConfig().getString(options.getLanguageSetterKey(), defaultLanguage);
            return configurations.getOrDefault(lang, defaultConfiguration);
        }

        return configurations.getOrDefault(pl.locale().toLanguageTag(), defaultConfiguration);
    }

    @Override
    protected @NotNull Configuration getConfiguration(@Nullable String lang) {
        return configurations.getOrDefault(Objects.requireNonNullElse(lang, defaultLanguage), defaultConfiguration);
    }
}
