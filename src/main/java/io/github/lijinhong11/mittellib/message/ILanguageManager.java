package io.github.lijinhong11.mittellib.message;

import java.util.List;
import java.util.Set;
import lombok.*;
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

    @NotNull
    ItemStack getMessagedItem(
            @NotNull Material material,
            @NotNull String sectionKey,
            @Nullable Player player,
            MessageReplacement... args);

    @NotNull
    String getParsedLocation(@Nullable CommandSender cs, @NotNull Location loc);

    @NotNull
    String getParsedBlockLocation(@Nullable CommandSender cs, @NotNull Location loc);

    String getParsedLocation(@Nullable CommandSender cs, double x, double y, double z);

    void reload();

    @NotNull
    Set<String> getTranslationKeys();

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
