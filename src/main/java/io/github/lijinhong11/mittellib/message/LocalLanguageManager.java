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
import org.jetbrains.annotations.Nullable;

/**
 * A class to manage language files and messages from local folders.
 */
public final class LocalLanguageManager extends AbstractLanguageManager {
    @Setter
    private Options options;

    public LocalLanguageManager(Plugin plugin) {
        this(plugin, new Options());
    }

    public LocalLanguageManager(Plugin plugin, Options options) {
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
    protected Configuration getConfiguration(CommandSender p) {
        if (!detectPlayerLocale || !(p instanceof Player pl)) {
            String lang = plugin.getConfig().getString(options.getLanguageSetterKey(), defaultLanguage);
            return configurations.getOrDefault(lang, defaultConfiguration);
        }

        return configurations.getOrDefault(pl.locale().toLanguageTag(), defaultConfiguration);
    }

    @Override
    protected Configuration getConfiguration(@Nullable String lang) {
        return configurations.getOrDefault(Objects.requireNonNullElse(lang, defaultLanguage), defaultConfiguration);
    }
}
