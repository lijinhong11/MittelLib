package io.github.lijinhong11.mittellib.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class ComponentUtils {
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacyAmpersand().toBuilder().hexColors().build();

    private static final Component RESET = Component.empty().decoration(TextDecoration.ITALIC, false);

    public static Component deserialize(String input) {
        return deserialize(null, input);
    }

    public static Component deserialize(@Nullable CommandSender cs, String input) {
        if (input == null) {
            return Component.empty();
        }

        input = StringUtils.parsePlaceholders(cs, input);

        return LEGACY.deserialize(input);
    }

    public static Component text(String input) {
        return RESET.append(Component.text(input));
    }

    public static String serialize(Component component) {
        return LEGACY.serialize(component);
    }
}
