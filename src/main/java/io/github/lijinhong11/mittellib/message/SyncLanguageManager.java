package io.github.lijinhong11.mittellib.message;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.utils.ConfigFileUtils;
import io.github.lijinhong11.mittellib.utils.StringUtils;
import java.io.File;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * A class to manage language files and messages and sync from jar.
 * <br>
 * Please use {@link MittelLib#getLanguageManager(Plugin)} to get language manager!
 */
public final class SyncLanguageManager extends AbstractLanguageManager {
    @Setter
    private boolean autoComplete;

    public SyncLanguageManager(Plugin plugin) {
        this(plugin, "en-US");
    }

    public SyncLanguageManager(Plugin plugin, String defaultLanguage) {
        this(plugin, defaultLanguage, true);
    }

    public SyncLanguageManager(Plugin plugin, String defaultLanguage, boolean autoComplete) {
        super(plugin, defaultLanguage);
        this.autoComplete = autoComplete;
        loadLanguages();
    }

    @Override
    protected void loadLanguages() {
        detectPlayerLocale = plugin.getConfig().getBoolean("detect-player-locale", true);

        File pluginFolder = plugin.getDataFolder();
        File languageFolder = new File(pluginFolder, "language");

        try {
            URL fileURL =
                    Objects.requireNonNull(plugin.getClass().getClassLoader().getResource("language/"));
            String jarPath = fileURL.toString().substring(0, fileURL.toString().indexOf("!/") + 2);
            languageFolder.mkdirs();
            URL jar = URI.create(jarPath).toURL();
            JarURLConnection jarCon = (JarURLConnection) jar.openConnection();
            JarFile jarFile = jarCon.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();

            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();
                String name = entry.getName();
                if (!name.startsWith("language/") || entry.isDirectory()) {
                    continue;
                }

                String realName = name.replaceFirst("language/", "");
                Path path = languageFolder.toPath().resolve(realName);
                if (!path.toFile().exists()) {
                    plugin.saveResource("language/" + realName, false);
                }
            }
        } catch (Exception ignored) {
        }

        defaultConfiguration =
                YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "language/en-US.yml"));

        File[] languageFiles = languageFolder.listFiles(f -> f.getName().endsWith(".yml"));
        if (languageFiles != null) {
            for (File languageFile : languageFiles) {
                String language = StringUtils.convertToRightLangCode(
                        languageFile.getName().replaceAll(".yml", ""));
                if (autoComplete) {
                    ConfigFileUtils.completeLangFile(plugin, "language/" + languageFile.getName());
                }
                configurations.put(language, YamlConfiguration.loadConfiguration(languageFile));
            }
        }
    }

    @Override
    protected Configuration getConfiguration(CommandSender p) {
        if (!detectPlayerLocale || !(p instanceof Player pl)) {
            String lang = plugin.getConfig().getString("language", defaultLanguage);
            return configurations.getOrDefault(lang, defaultConfiguration);
        }

        return configurations.getOrDefault(pl.locale().toLanguageTag(), defaultConfiguration);
    }

    @Override
    protected Configuration getConfiguration(@Nullable String lang) {
        return configurations.getOrDefault(Objects.requireNonNullElse(lang, defaultLanguage), defaultConfiguration);
    }
}
