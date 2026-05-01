package io.github.lijinhong11.mittellib.utils;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.message.SyncLanguageManager;
import io.github.miniplaceholders.api.MiniPlaceholders;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class StringUtils {
    private static final PlainTextComponentSerializer COMPONENT_PLAIN = PlainTextComponentSerializer.plainText();

    public static String toBooleanStatus(@Nullable CommandSender cs, boolean b) {
        SyncLanguageManager lm = MittelLib.getInstance().getLanguageManager();
        return b ? lm.getMsg(cs, "common.enabled") : lm.getMsg(cs, "common.disabled");
    }

    public static String parsePlaceholders(@NotNull String text) {
        return parsePlaceholders(null, text);
    }

    public static String parsePlaceholders(@Nullable CommandSender cs, @NotNull String text) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(cs instanceof Player p ? p : null, text);
        }

        Component result = Component.text(text);

        if (Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            String plain = COMPONENT_PLAIN.serialize(result);
            if (cs != null) {
                return COMPONENT_PLAIN.serialize(MiniMessage.miniMessage()
                        .deserialize(plain, cs, MiniPlaceholders.audienceGlobalPlaceholders()));
            } else {
                return COMPONENT_PLAIN.serialize(
                        MiniMessage.miniMessage().deserialize(plain, MiniPlaceholders.globalPlaceholders()));
            }
        }

        return text;
    }

    public static String convertToRightLangCode(String lang) {
        if (lang == null || lang.isBlank()) return "en-US";
        String[] split = lang.split("-");
        if (split.length == 1) {
            String[] split2 = lang.split("_");
            if (split2.length == 1) return lang;
            return lang.replace(split2[1], split2[1].toUpperCase());
        }
        return lang.replace(split[1], split[1].toUpperCase());
    }

    public static String compress(@NotNull String input) {
        if (input.isEmpty()) {
            return "";
        }

        byte[] data = input.getBytes(StandardCharsets.UTF_8);

        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        byte[] buffer = new byte[1024];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                baos.write(buffer, 0, count);
            }

            deflater.end();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            deflater.end();
            throw new RuntimeException("Failed to compress string", e);
        }
    }

    public static String decompress(@NotNull String compressed) {
        if (compressed.isEmpty()) {
            return "";
        }

        byte[] data = Base64.getDecoder().decode(compressed);
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        byte[] buffer = new byte[1024];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                baos.write(buffer, 0, count);
            }

            inflater.end();
            return baos.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            inflater.end();
            throw new RuntimeException("Failed to decompress string", e);
        }
    }
}
