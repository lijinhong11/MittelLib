package io.github.lijinhong11.mittellib.message;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.utils.ComponentUtils;
import io.github.lijinhong11.mittellib.utils.ConfigFileUtils;
import io.github.lijinhong11.mittellib.utils.StringUtils;
import java.io.File;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 * A class to manage language files and messages and sync from jar.
 * <br>
 * Please use {@link MittelLib#getLanguageManager(Plugin)} to get language manager!
 */
public final class SyncLanguageManager implements ILanguageManager {
    private final Plugin plugin;
    private final String defaultLanguage;

    private final Map<String, YamlConfiguration> configurations = new HashMap<>();

    @Setter
    private boolean detectPlayerLocale = true;
    @Setter
    private boolean autoComplete;

    private YamlConfiguration defaultConfiguration;

    @Getter
    @Setter
    private ILanguageManager fallback;

    public SyncLanguageManager(Plugin plugin) {
        this(plugin, "en-US");
    }

    public SyncLanguageManager(Plugin plugin, String defaultLanguage) {
        this(plugin, defaultLanguage, true);
    }

    public SyncLanguageManager(Plugin plugin, String defaultLanguage, boolean autoComplete) {
        this.plugin = plugin;
        this.defaultLanguage = defaultLanguage;
        this.autoComplete = autoComplete;

        loadLanguages();
    }

    private static Component parseToComponent(@Nullable CommandSender sender, String msg) {
        return ComponentUtils.deserialize(sender, msg);
    }

    private static List<Component> parseToComponentList(List<String> msgList) {
        return msgList.stream().map(ComponentUtils::deserialize).toList();
    }

    private static List<Component> parseToComponentList(@Nullable CommandSender sender, List<String> msgList) {
        return msgList.stream().map(msg -> parseToComponent(sender, msg)).toList();
    }

    private void loadLanguages() {
        detectPlayerLocale = plugin.getConfig().getBoolean("detect-player-locale", true);

        File pluginFolder = plugin.getDataFolder();

        URL fileURL = Objects.requireNonNull(plugin.getClass().getClassLoader().getResource("language/"));
        String jarPath = fileURL.toString().substring(0, fileURL.toString().indexOf("!/") + 2);
        File languageFolder = new File(pluginFolder, "language");

        try {
            languageFolder.mkdirs();
            URL jar = URI.create(jarPath).toURL();
            JarURLConnection jarCon = (JarURLConnection) jar.openConnection();
            JarFile jarFile = jarCon.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();

            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();
                String name = entry.getName();
                if (!name.startsWith("language/")) {
                    continue;
                }

                if (!entry.isDirectory()) {
                    String realName = name.replaceFirst("language/", "");
                    Path path = languageFolder.toPath().resolve(realName);
                    if (!path.toFile().exists()) {
                        plugin.saveResource("language/" + realName, false);
                    } else {
                        if (autoComplete) {
                            ConfigFileUtils.completeLangFile(plugin, "language/" + realName);
                        } else {
                            path.toFile().createNewFile();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        defaultConfiguration =
                YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "language/en-US.yml"));

        File[] languageFiles = languageFolder.listFiles(f -> f.getName().endsWith(".yml"));
        if (languageFiles != null) {
            for (File languageFile : languageFiles) {
                String language = StringUtils.convertToRightLangCode(languageFile.getName().replaceAll(".yml", ""));
                if (autoComplete) {
                    ConfigFileUtils.completeLangFile(plugin, "language/" + languageFile.getName());
                }
                configurations.put(language, YamlConfiguration.loadConfiguration(languageFile));
            }
        }
    }

    @Override
    public void sendMessage(@NonNull CommandSender commandSender, String key, MessageReplacement... args) {
        commandSender.sendMessage(parseToComponent(commandSender, getMsg(commandSender, key, args)));
    }

    @Override
    public void sendMessage(
            @NotNull CommandSender commandSender, String key, ClickEvent clickEvent, MessageReplacement... args) {
        commandSender.sendMessage(
                parseToComponent(commandSender, getMsg(commandSender, key, args)).clickEvent(clickEvent));
    }

    @Override
    public void sendMessages(@NotNull CommandSender commandSender, String key, MessageReplacement... args) {
        for (String msg : getMsgList(commandSender, key, args)) {
            commandSender.sendMessage(parseToComponent(commandSender, msg));
        }
    }

    @Override
    public Component getMsgComponent(@Nullable CommandSender commandSender, String key, MessageReplacement... args) {
        return parseToComponent(commandSender, getMsg(commandSender, key, args));
    }

    @Override
    public Component getMsgComponentByLanguage(@Nullable String lang, String key, MessageReplacement... args) {
        return ComponentUtils.deserialize(getMsgByLanguage(lang, key, args));
    }

    @Override
    public List<Component> getMsgComponentList(
            @Nullable CommandSender commandSender, String key, MessageReplacement... args) {
        return parseToComponentList(commandSender, getMsgList(commandSender, key, args));
    }

    @Override
    public List<Component> getMsgComponentListByLanguage(
            @Nullable String lang, String key, MessageReplacement... args) {
        return parseToComponentList(getMsgListByLanguage(lang, key, args));
    }

    @Override
    public String getMsg(@Nullable CommandSender sender, String key, MessageReplacement... args) {
        String msg = getConfiguration(sender).getString(key);

        if (msg == null) {
            if (fallback != null) {
                return fallback.getMsg(sender, key, args);
            }

            return key;
        }

        for (MessageReplacement arg : args) {
            msg = arg.parse(msg);
        }

        msg = StringUtils.parsePlaceholders(sender, msg);

        return msg;
    }

    @Override
    public List<String> getMsgList(@Nullable CommandSender commandSender, String key, MessageReplacement... args) {
        List<String> msgList = getConfiguration(commandSender).getStringList(key);
        for (MessageReplacement arg : args) {
            msgList.replaceAll(arg::parse);
        }

        return msgList;
    }

    @Override
    public String getMsgByLanguage(@Nullable String lang, String key, MessageReplacement... args) {
        String msg = getConfiguration(lang).getString(key);
        if (msg == null) {
            return key;
        }

        for (MessageReplacement arg : args) {
            msg = arg.parse(msg);
        }

        return msg;
    }

    @Override
    public List<String> getMsgListByLanguage(@Nullable String lang, String key, MessageReplacement... args) {
        List<String> msgList = getConfiguration(lang).getStringList(key);
        for (MessageReplacement arg : args) {
            msgList.replaceAll(arg::parse);
        }

        return msgList;
    }

    @Override
    public @NotNull ItemStack getMessagedItem(
            @NotNull Material material,
            @NotNull String sectionKey,
            @Nullable Player player,
            MessageReplacement... args) {
        ItemStack is = new ItemStack(material);
        ItemMeta meta = is.getItemMeta();
        meta.displayName(getMsgComponent(player, sectionKey + ".name", args));
        meta.lore(getMsgComponentList(player, sectionKey + ".lore", args));
        is.setItemMeta(meta);
        return is;
    }

    @Override
    public @NonNull String getParsedLocation(@Nullable CommandSender cs, @NotNull Location loc) {
        return getParsedLocation(cs, loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public @NonNull String getParsedBlockLocation(@Nullable CommandSender cs, @NotNull Location loc) {
        return getParsedLocation(cs, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public String getParsedLocation(@Nullable CommandSender cs, double x, double y, double z) {
        MessageReplacement xm = MessageReplacement.replace("%x%", String.valueOf(x));
        MessageReplacement ym = MessageReplacement.replace("%y%", String.valueOf(y));
        MessageReplacement zm = MessageReplacement.replace("%z%", String.valueOf(z));

        return MittelLib.getInstance().getLanguageManager().getMsg(cs, "common.location-format", xm, ym, zm);
    }

    @Override
    public void reload() {
        loadLanguages();
    }

    public @NotNull Set<String> getTranslationKeys() {
        return defaultConfiguration.getKeys(true);
    }

    private Configuration getConfiguration(CommandSender p) {
        if (!detectPlayerLocale || !(p instanceof Player pl)) {
            String lang = plugin.getConfig().getString("language", defaultLanguage);
            return configurations.getOrDefault(lang, defaultConfiguration);
        }

        return configurations.getOrDefault(pl.locale().toLanguageTag(), defaultConfiguration);
    }

    private Configuration getConfiguration(String lang) {
        return configurations.getOrDefault(Objects.requireNonNullElse(lang, defaultLanguage), defaultConfiguration);
    }
}
