package io.github.lijinhong11.mittellib.message;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.utils.ComponentUtils;
import io.github.lijinhong11.mittellib.utils.StringUtils;
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

import java.io.File;
import java.util.*;

/**
 * A class to manage language files and messages from local folders.
 */
public final class LocalLanguageManager implements ILanguageManager {
    private final Plugin plugin;

    private final Map<String, YamlConfiguration> configurations = new HashMap<>();

    @Setter
    private Options options;

    private YamlConfiguration defaultConfiguration;

    @Getter
    @Setter
    private ILanguageManager fallback;

    public LocalLanguageManager(Plugin plugin) {
        this(plugin, new Options());
    }

    public LocalLanguageManager(Plugin plugin, Options options) {
        this.plugin = plugin;
        this.options = options;

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
        File pluginFolder = plugin.getDataFolder();

        File languageFolder = new File(pluginFolder, "language");

        defaultConfiguration =
                YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "language/en-US.yml"));

        File[] languageFiles = languageFolder.listFiles(f -> f.getName().endsWith(".yml"));
        if (languageFiles != null) {
            for (File languageFile : languageFiles) {
                String language = StringUtils.convertToRightLangCode(languageFile.getName().replaceAll(".yml", ""));
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

    @Override
    public @NotNull Set<String> getTranslationKeys() {
        return defaultConfiguration.getKeys(true);
    }

    private Configuration getConfiguration(CommandSender p) {
        if (!options.isDetectPlayerLocale() || !(p instanceof Player pl)) {
            String lang = plugin.getConfig().getString(options.getLanguageSetterKey(), options.getDefaultLanguage());
            return configurations.getOrDefault(lang, defaultConfiguration);
        }

        return configurations.getOrDefault(pl.locale().toLanguageTag(), defaultConfiguration);
    }

    private Configuration getConfiguration(String lang) {
        return configurations.getOrDefault(Objects.requireNonNullElse(lang, options.getDefaultLanguage()), defaultConfiguration);
    }
}
