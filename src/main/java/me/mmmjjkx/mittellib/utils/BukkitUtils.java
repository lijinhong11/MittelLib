package me.mmmjjkx.mittellib.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

            return null;
        }
    }
}
