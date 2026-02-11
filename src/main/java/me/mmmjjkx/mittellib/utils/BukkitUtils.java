package me.mmmjjkx.mittellib.utils;

import lombok.experimental.UtilityClass;
import me.mmmjjkx.mittellib.MittelLib;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@UtilityClass
public class BukkitUtils {
    public static @Nullable Material getMaterial(@NotNull String name) {
        return getMaterial(name, null);
    }

    public static @Nullable Material getMaterial(@NotNull String name, @Nullable Material def) {
        return EnumUtils.readEnum(Material.class, name, def);
    }

    public static @Nullable NamespacedKey getNamespacedKey(@NotNull String namespacedKey) {
        if (!namespacedKey.matches(Patterns.NAMESPACED_KEY)) {
            String minecraft = "minecraft:" + namespacedKey;
            if (minecraft.matches(Patterns.NAMESPACED_KEY)) {
                return NamespacedKey.minecraft(namespacedKey);
            }

            MittelLib.getInstance()
                    .getLogger()
                    .severe("The string doesn't match NamespacedKey's format: " + namespacedKey);
            return null;
        }

        return NamespacedKey.fromString(namespacedKey);
    }

    public static @NotNull List<NamespacedKey> getNamespacedKeys(@NotNull Iterable<String> namespacedKeys) {
        List<NamespacedKey> keys = new ArrayList<>();
        for (String s : namespacedKeys) {
            NamespacedKey key = getNamespacedKey(s);
            if (key != null) {
                keys.add(key);
            }
        }

        return keys;
    }

    public static @Nullable PotionEffect readPotionEffect(@NotNull ConfigurationSection cs) {
        NamespacedKey potionEffectTypeKey = getNamespacedKey(cs.getString("type", "null"));
        if (potionEffectTypeKey == null) {
            return null;
        }

        PotionEffectType pet = Registry.EFFECT.get(potionEffectTypeKey);
        if (pet == null) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot find a potion effect type with key " + potionEffectTypeKey.asString());
            return null;
        }

        int duration = cs.getInt("duration");
        int amplifier = cs.getInt("amplifier");
        boolean ambient = cs.getBoolean("ambient", true);
        boolean particle = cs.getBoolean("particle", true);
        boolean icon = cs.getBoolean("icon", true);

        return new PotionEffect(pet, duration, amplifier, ambient, particle, icon);
    }

    public static @Nullable PotionEffect readPotionEffect(@NotNull Map<String, Object> map) {
        NamespacedKey potionEffectTypeKey = getNamespacedKey((String) map.getOrDefault("type", "null"));
        if (potionEffectTypeKey == null) {
            return null;
        }

        PotionEffectType pet = Registry.EFFECT.get(potionEffectTypeKey);
        if (pet == null) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot find a potion effect type with key " + potionEffectTypeKey.asString());
            return null;
        }

        int duration = (int) map.get("duration");
        int amplifier = (int) map.get("amplifier");
        boolean ambient = (boolean) map.getOrDefault("ambient", true);
        boolean particle = (boolean) map.getOrDefault("particle", true);
        boolean icon = (boolean) map.getOrDefault("icon", true);

        return new PotionEffect(pet, duration, amplifier, ambient, particle, icon);
    }

    public static @NotNull List<Color> toColors(List<Map<?, ?>> colorMaps) {
        List<Color> colors = new ArrayList<>();
        for (Map<?, ?> color : colorMaps) {
            Map<String, Integer> colorMap = (Map<String, Integer>) color;
            Color bukkit = Color.fromARGB(colorMap.getOrDefault("alpha", 255), colorMap.get("red"), colorMap.get("green"), colorMap.get("blue"));
            colors.add(bukkit);
        }

        return colors;
    }

    public static List<Map<String, Integer>> writeColor(List<Color> colors) {
        return colors.stream()
                .filter(Objects::nonNull)
                .map(c -> {
                    Map<String, Integer> color = new HashMap<>();
                    color.put("alpha", c.getAlpha());
                    color.put("red", c.getRed());
                    color.put("green", c.getGreen());
                    color.put("blue", c.getBlue());
                    return color;
                }).toList();
    }
}
