package io.github.lijinhong11.mittellib.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import lombok.experimental.UtilityClass;
import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.utils.constant.Patterns;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;

@UtilityClass
public class BukkitUtils {
    public static @Nullable Material getMaterial(@NotNull String name) {
        return getMaterial(name, null);
    }

    public static @Nullable Material getMaterial(@NotNull String name, @Nullable Material def) {
        return EnumUtils.readEnum(Material.class, name, def);
    }

    public static @Nullable String getProfileSkinURL(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof SkullMeta skullMeta)) {
            return null;
        }

        PlayerProfile profile = skullMeta.getPlayerProfile();
        if (profile == null) {
            return null;
        }

        PlayerTextures textures = profile.getTextures();
        if (textures.getSkin() == null) {
            return null;
        }

        return textures.getSkin().toString();
    }

    public static void setProfileBySkinURL(@NotNull ItemStack itemStack, @NotNull String url) {
        if (!(itemStack.getItemMeta() instanceof SkullMeta skullMeta)) {
            return;
        }

        PlayerProfile profile = Bukkit.createProfile("MITTEL_LIB_LOL");

        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(URI.create(url).toURL());
            profile.setTextures(textures);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        skullMeta.setPlayerProfile(profile);
    }

    public static @Nullable NamespacedKey getNamespacedKey(@NotNull String namespacedKey) {
        if (namespacedKey.equals("null")) {
            return null;
        }

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
