package io.github.lijinhong11.mittellib.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.github.lijinhong11.mittellib.MittelLib;
import lombok.experimental.UtilityClass;
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
    public static @NotNull Material getMaterial(@NotNull String name) {
        return getMaterial(name, Material.BARRIER);
    }

    public static @NotNull Material getMaterial(@NotNull String name, @Nullable Material def) {
        if (name.isBlank()) {
            return def == null ? Material.BARRIER : def;
        }

        try {
            Material material = Material.matchMaterial(name);
            return material == null ? (def == null ? Material.BARRIER : def) : material;
        } catch (Exception ignored) {
            return def == null ? Material.BARRIER : def;
        }
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
        return textures.getSkin() == null ? null : textures.getSkin().toString();
    }

    public static void setProfileBySkinURL(@NotNull ItemStack itemStack, @NotNull String url) {
        if (!(itemStack.getItemMeta() instanceof SkullMeta skullMeta)) {
            return;
        }

        try {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(URI.create(url).toURL());
            profile.setTextures(textures);

            skullMeta.setPlayerProfile(profile);
            itemStack.setItemMeta(skullMeta);
        } catch (MalformedURLException e) {
            MittelLib.getInstance().getLogger().severe("Invalid skin URL: " + url);
        }
    }

    public static @Nullable NamespacedKey getNamespacedKey(@Nullable String input) {
        if (input == null || input.isBlank() || input.equalsIgnoreCase("null")) {
            return null;
        }

        NamespacedKey key = NamespacedKey.fromString(input);
        if (key != null) {
            return key;
        }

        return NamespacedKey.minecraft(input);
    }

    public static @NotNull List<NamespacedKey> getNamespacedKeys(@NotNull Iterable<String> keys) {
        List<NamespacedKey> list = new ArrayList<>();
        for (String s : keys) {
            NamespacedKey key = getNamespacedKey(s);
            if (key != null) {
                list.add(key);
            }
        }
        return list;
    }

    public static @Nullable PotionEffect readPotionEffect(@NotNull ConfigurationSection cs) {
        NamespacedKey key = getNamespacedKey(cs.getString("type"));
        if (key == null) return null;

        PotionEffectType type = Registry.EFFECT.get(key);
        if (type == null) {
            MittelLib.getInstance().getLogger()
                    .severe("Unknown potion effect type: " + key.asString());
            return null;
        }

        int duration = cs.getInt("duration");
        if (duration <= 0) return null;

        int amplifier = cs.getInt("amplifier", 0);
        boolean ambient = cs.getBoolean("ambient", true);
        boolean particle = cs.getBoolean("particle", true);
        boolean icon = cs.getBoolean("icon", true);

        return new PotionEffect(type, duration, amplifier, ambient, particle, icon);
    }

    public static @Nullable PotionEffect readPotionEffect(@NotNull Map<String, Object> map) {
        Object typeObj = map.get("type");
        if (!(typeObj instanceof String typeStr)) return null;

        NamespacedKey key = getNamespacedKey(typeStr);
        if (key == null) return null;

        PotionEffectType type = Registry.EFFECT.get(key);
        if (type == null) return null;

        Number durationNum = asNumber(map.get("duration"));
        if (durationNum == null || durationNum.intValue() <= 0) return null;

        Number amplifierNum = asNumber(map.getOrDefault("amplifier", 0));

        boolean ambient = asBoolean(map.getOrDefault("ambient", true));
        boolean particle = asBoolean(map.getOrDefault("particle", true));
        boolean icon = asBoolean(map.getOrDefault("icon", true));

        return new PotionEffect(
                type,
                durationNum.intValue(),
                amplifierNum == null ? 0 : amplifierNum.intValue(),
                ambient,
                particle,
                icon
        );
    }

    public static @NotNull Map<String, Object> writePotionEffect(@NotNull PotionEffect effect) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", effect.getType().key().asString());
        map.put("duration", effect.getDuration());
        map.put("amplifier", effect.getAmplifier());
        map.put("ambient", effect.isAmbient());
        map.put("particle", effect.hasParticles());
        map.put("icon", effect.hasIcon());
        return map;
    }


    public static @NotNull List<Color> toColors(@NotNull List<Map<?, ?>> maps) {
        List<Color> list = new ArrayList<>();

        for (Map<?, ?> raw : maps) {
            Number r = asNumber(raw.get("red"));
            Number g = asNumber(raw.get("green"));
            Number b = asNumber(raw.get("blue"));
            Number a = asNumber(raw.get("alpha"));

            if (r == null || g == null || b == null) continue;

            list.add(Color.fromARGB(
                    a == null ? 255 : a.intValue(),
                    r.intValue(),
                    g.intValue(),
                    b.intValue()
            ));
        }

        return list;
    }

    public static @NotNull List<Map<String, Integer>> writeColors(@NotNull List<Color> colors) {
        List<Map<String, Integer>> list = new ArrayList<>();

        for (Color c : colors) {
            if (c == null) continue;

            Map<String, Integer> map = new HashMap<>();
            map.put("alpha", c.getAlpha());
            map.put("red", c.getRed());
            map.put("green", c.getGreen());
            map.put("blue", c.getBlue());

            list.add(map);
        }

        return list;
    }

    public static void writeLocationSection(@NotNull ConfigurationSection cs, @Nullable Location loc) {
        if (loc == null || loc.getWorld() == null) return;

        cs.set("world", loc.getWorld().getName());
        cs.set("x", loc.getX());
        cs.set("y", loc.getY());
        cs.set("z", loc.getZ());
        cs.set("yaw", loc.getYaw());
        cs.set("pitch", loc.getPitch());
    }

    public static @Nullable Location readLocation(@NotNull ConfigurationSection cs) {
        String worldName = cs.getString("world");
        if (worldName == null || worldName.isBlank()) {
            MittelLib.getInstance().getLogger()
                    .severe("Cannot read location: world is missing");
            return null;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            MittelLib.getInstance().getLogger()
                    .severe("Cannot read location: world " + worldName + " does not exist");
            return null;
        }

        double x = cs.getDouble("x");
        double y = cs.getDouble("y");
        double z = cs.getDouble("z");
        float yaw = (float) cs.getDouble("yaw", 0);
        float pitch = (float) cs.getDouble("pitch", 0);

        return new Location(world, x, y, z, yaw, pitch);
    }

    private static @Nullable Number asNumber(Object obj) {
        return obj instanceof Number n ? n : null;
    }

    private static boolean asBoolean(Object obj) {
        return obj instanceof Boolean b && b;
    }
}