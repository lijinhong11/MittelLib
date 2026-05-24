package io.github.lijinhong11.mittellib.utils;

import io.github.miniplaceholders.api.MiniPlaceholders;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class ComponentUtils {
    private static final TagResolver COMBINE_TAG_RESOLVER = TagResolver.resolver(
            MiniMessage.miniMessage().tags(),
            MiniPlaceholders.globalPlaceholders(),
            MiniPlaceholders.audiencePlaceholders());

    private static String legacyReplacement(char code) {
        return switch (code) {
            case '0' -> "<black>";
            case '1' -> "<dark_blue>";
            case '2' -> "<dark_green>";
            case '3' -> "<dark_aqua>";
            case '4' -> "<dark_red>";
            case '5' -> "<dark_purple>";
            case '6' -> "<gold>";
            case '7' -> "<gray>";
            case '8' -> "<dark_gray>";
            case '9' -> "<blue>";
            case 'a' -> "<green>";
            case 'b' -> "<aqua>";
            case 'c' -> "<red>";
            case 'd' -> "<light_purple>";
            case 'e' -> "<yellow>";
            case 'f' -> "<white>";
            case 'k' -> "<magic>";
            case 'l' -> "<bold>";
            case 'm' -> "<strikethrough>";
            case 'n' -> "<underline>";
            case 'o' -> "<italic>";
            case 'r' -> "<reset>";
            default -> null;
        };
    }

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

        if (cs != null) {
            return MINI.deserialize(input, cs, COMBINE_TAG_RESOLVER);
        } else {
            return MINI.deserialize(input, COMBINE_TAG_RESOLVER);
        }
    }

    public static String fromLegacy(String legacy, String character) {
        StringBuilder sb = new StringBuilder(legacy.length());
        char prefix = character.charAt(0);

        for (int i = 0; i < legacy.length(); i++) {
            char c = legacy.charAt(i);

            if (c == prefix && i + 1 < legacy.length()) {
                char next = legacy.charAt(i + 1);

                if (next == '#') {
                    if (i + 7 < legacy.length()) {
                        sb.append("<").append(legacy, i + 1, i + 8).append(">");
                        i += 7;
                        continue;
                    }
                } else {
                    String replace = legacyReplacement(next);
                    if (replace != null) {
                        sb.append(replace);
                        i++;
                        continue;
                    }
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
