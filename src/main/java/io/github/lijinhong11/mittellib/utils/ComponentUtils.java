package io.github.lijinhong11.mittellib.utils;

import io.github.miniplaceholders.api.MiniPlaceholders;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

@UtilityClass
public class ComponentUtils {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer
            .legacyAmpersand()
            .toBuilder()
            .hexColors()
            .build();

    private static final Component RESET = Component.empty().decoration(TextDecoration.ITALIC, false);

    public static Component deserialize(String input) {
        if (input == null) {
            return Component.empty();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            input = PlaceholderAPI.setPlaceholders(null, input);
        }

        Component result = LEGACY.deserialize(input);

        if (Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            String mini = MiniMessage.miniMessage().serialize(result);
            return MiniMessage.miniMessage().deserialize(mini, MiniPlaceholders.globalPlaceholders());
        }

        return result;
    }

    public static Component text(String input) {
        return RESET.append(Component.text(input));
    }

    public static String serialize(Component component) {
        return LEGACY.serialize(component);
    }

    public static String serializeLegacy(Component component) {
        return LEGACY.serialize(component);
    }
}
