package me.mmmjjkx.mittellib.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ComponentUtils {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer
            .legacyAmpersand()
            .toBuilder()
            .hexColors()
            .build();

    private static final Component RESET = Component.empty().decoration(TextDecoration.ITALIC, false);

    private ComponentUtils() {
    }

    public static Component deserialize(String input) {
        if (input == null) {
            return Component.empty();
        }

        //if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        //    input = PlaceholderAPI.setPlaceholders(null, input);
        //}

        Component result = LEGACY.deserialize(input);

        //if (Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
        //    String mini = MiniMessage.miniMessage().serialize(result);
        //    return MiniMessage.miniMessage().deserialize(mini, MiniPlaceholders.getGlobalPlaceholders());
        //}

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
