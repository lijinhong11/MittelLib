package io.github.lijinhong11.mittellib.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@UtilityClass
public class ComponentUtils {
    public static final Map<String, String> REPLACES = Map.ofEntries(
        Map.entry("0", "<black>"),
        Map.entry("1", "<dark_blue>"),
        Map.entry("2", "<dark_green>"),
        Map.entry("3", "<dark_aqua>"),
        Map.entry("4", "<dark_red>"),
        Map.entry("5", "<dark_purple>"),
        Map.entry("6", "<gold>"),
        Map.entry("7", "<gray>"),
        Map.entry("8", "<dark_gray>"),
        Map.entry("9", "<blue>"),
        Map.entry("a", "<green>"),
        Map.entry("b", "<aqua>"),
        Map.entry("c", "<red>"),
        Map.entry("d", "<light_purple>"),
        Map.entry("e", "<yellow>"),
        Map.entry("f", "<white>"),
        Map.entry("k", "<magic>"),
        Map.entry("l", "<bold>"),
        Map.entry("m", "<strikethrough>"),
        Map.entry("n", "<underline>"),
        Map.entry("o", "<italic>"),
        Map.entry("r", "<reset>")
    );

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private static final Component RESET = Component.empty().decoration(TextDecoration.ITALIC, false);

    public static Component deserialize(String input) {
        return deserialize(null, input);
    }

    public static Component deserialize(@Nullable CommandSender cs, String input) {
        if (input == null) {
            return Component.empty();
        }

        input = StringUtils.parsePlaceholders(cs, input);
        input = fromLegacy(input, "&");
        input = fromLegacy(input, "§");

        return MINI.deserialize(input);
    }

    public static String fromLegacy(String legacy, String character) {
        StringBuilder sb = new StringBuilder(legacy.length());

        for (int i = 0; i < legacy.length(); i++) {
            char c = legacy.charAt(i);

            if (c == character.charAt(0)) {
                if (i + 1 < legacy.length()) {
                    String key = String.valueOf(legacy.charAt(i + 1));
                    String replace = REPLACES.get(key);

                    if (replace != null) {
                        sb.append(replace);
                        i++;
                        continue;
                    }
                }

                if (i + 7 < legacy.length() && legacy.charAt(i + 1) == '#') {
                    String hex = legacy.substring(i + 1, i + 8);
                    sb.append("<").append(hex).append(">");
                    i += 7;
                    continue;
                }
            }

            sb.append(c);
        }

        return sb.toString();
    }

    public static Component text(String input) {
        return RESET.append(Component.text(input));
    }

    public static String serialize(Component component) {
        return MINI.serialize(component);
    }
}
