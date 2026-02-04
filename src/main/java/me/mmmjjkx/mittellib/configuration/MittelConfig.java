package me.mmmjjkx.mittellib.configuration;

import me.mmmjjkx.mittellib.item.MittelItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MittelConfig extends YamlConfiguration {
    public MittelConfig() {
        super();
    }

    @Override
    public @Nullable ItemStack getItemStack(@NotNull String path) {
        return getItemStack(path, null);
    }

    @Override
    public @Nullable ItemStack getItemStack(@NotNull String path, @Nullable ItemStack def) {
        MittelItem mittelItem = getRWObject(path, MittelItem.class);
        if (mittelItem == null) {
            return null;
        }

        return mittelItem.get();
    }

    public <T extends ReadWriteObject> T getRWObject(@NotNull String path, @NotNull Class<T> clazz) {
        return getRWObject(path, clazz, null);
    }

    public <T extends ReadWriteObject> T getRWObject(@NotNull String path, @NotNull Class<T> clazz, @Nullable T def) {
        ConfigurationSection cs = getConfigurationSection(path);
        if (cs == null) {
            return def;
        }

        return ReadWriteObject.read(clazz, cs);
    }
}
