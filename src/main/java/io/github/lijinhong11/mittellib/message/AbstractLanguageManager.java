package io.github.lijinhong11.mittellib.message;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.utils.ComponentUtils;
import io.github.lijinhong11.mittellib.utils.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

abstract class AbstractLanguageManager implements ILanguageManager {
    protected final Plugin plugin;
    protected final String defaultLanguage;

    protected final Map<String, YamlConfiguration> configurations = new HashMap<>();

    @Setter
    protected boolean detectPlayerLocale = true;

    protected YamlConfiguration defaultConfiguration;

    @Getter
    @Setter
    private ILanguageManager fallback;

    protected AbstractLanguageManager(Plugin plugin, String defaultLanguage) {
        this.plugin = plugin;
        this.defaultLanguage = defaultLanguage;
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

    @Override
    public void sendMessage(@NonNull CommandSender commandSender, String key, MessageReplacement... args) {
        commandSender.sendMessage(parseToComponent(commandSender, getMsg(commandSender, key, args)));
    }

    @Override
    public void sendMessage(
            @NotNull CommandSender commandSender, String key, ClickEvent clickEvent, MessageReplacement... args) {
        commandSender.sendMessage(parseToComponent(commandSender, getMsg(commandSender, key, args))
                .clickEvent(clickEvent));
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
        configurations.clear();
        loadLanguages();
    }

    @Override
    public @NotNull Set<String> getTranslationKeys() {
        return defaultConfiguration.getKeys(true);
    }

    protected abstract Configuration getConfiguration(CommandSender sender);

    protected abstract Configuration getConfiguration(String lang);

    protected abstract void loadLanguages();
}
