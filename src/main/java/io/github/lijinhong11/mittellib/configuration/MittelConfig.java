package io.github.lijinhong11.mittellib.configuration;

import lombok.Getter;
import lombok.Setter;
import io.github.lijinhong11.mittellib.item.MittelItem;
import io.github.lijinhong11.mittellib.utils.NumberUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MittelConfig {
    private final YamlConfiguration backend;

    @Getter
    @Setter
    private File file;

    public MittelConfig() {
        this(new YamlConfiguration());
    }

    public MittelConfig(@NotNull YamlConfiguration configuration) {
        this.backend = configuration;
    }

    public MittelConfig(@NotNull YamlConfiguration configuration, @Nullable File file) {
        this.backend = configuration;
        this.file = file;
    }

    public int getUnsignedInt(@NotNull String path) {
        int i = getInt(path);
        return NumberUtils.asUnsigned(i);
    }

    public int getUnsignedInt(@NotNull String path, int def) {
        int i = getInt(path, def);
        return NumberUtils.asUnsigned(i);
    }

    public long getUnsignedLong(@NotNull String path) {
        long l = getLong(path);
        return NumberUtils.asUnsigned(l);
    }

    public long getUnsignedLong(@NotNull String path, long def) {
        long l = getLong(path, def);
        return NumberUtils.asUnsigned(l);
    }

    public double getUnsignedDouble(@NotNull String path) {
        double d = getDouble(path);
        return NumberUtils.asUnsigned(d);
    }

    public double getUnsignedDouble(@NotNull String path, double def) {
        double d = getDouble(path, def);
        return NumberUtils.asUnsigned(d);
    }

    public float getUnsignedFloat(@NotNull String path) {
        float f = getFloat(path);
        return NumberUtils.asUnsigned(f);
    }

    public float getUnsignedFloat(@NotNull String path, float def) {
        float f = getFloat(path, def);
        return NumberUtils.asUnsigned(f);
    }

    public @Nullable ItemStack getItemStack(@NotNull String path) {
        return getItemStack(path, null);
    }

    public @Nullable ItemStack getItemStack(@NotNull String path, @Nullable ItemStack def) {
        ConfigurationSection cs = getConfigurationSection(path);
        if (cs == null) {
            return def;
        }

        MittelItem mittelItem = MittelItem.readFromSection(cs);

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

    public int getInt(@NotNull String path) {
        return backend.getInt(path);
    }

    public int getInt(@NotNull String path, int def) {
        return backend.getInt(path, def);
    }

    public long getLong(@NotNull String path) {
        return backend.getLong(path);
    }

    public long getLong(@NotNull String path, long def) {
        return backend.getLong(path, def);
    }

    public double getDouble(@NotNull String path) {
        return backend.getDouble(path);
    }

    public double getDouble(@NotNull String path, double def) {
        return backend.getDouble(path, def);
    }

    public float getFloat(@NotNull String path) {
        return (float) getDouble(path);
    }

    public float getFloat(@NotNull String path, float def) {
        return (float) getDouble(path, def);
    }

    public boolean isInt(@NotNull String path) {
        return backend.isInt(path);
    }

    public boolean isLong(@NotNull String path) {
        return backend.isLong(path);
    }

    public boolean isDouble(@NotNull String path) {
        return backend.isDouble(path);
    }

    public boolean isFloat(@NotNull String path) {
        return isDouble(path);
    }

    public boolean isSection(@NotNull String path) {
        return backend.isConfigurationSection(path);
    }

    public @Nullable ConfigurationSection getConfigurationSection(@NotNull String path) {
        return backend.getConfigurationSection(path);
    }

    public @NotNull ConfigurationSection createSection(@NotNull String path) {
        return backend.createSection(path);
    }

    public @NotNull ConfigurationSection getSectionOrCreate(@NotNull String path) {
        ConfigurationSection cs = backend.getConfigurationSection(path);
        return cs == null ? createSection(path) : cs;
    }

    public boolean contains(@NotNull String path) {
        return backend.contains(path);
    }

    public Set<String> getKeys(boolean deep) {
        return backend.getKeys(deep);
    }

    public Set<String> getKeys(@NotNull String sectionPath, boolean deep) {
        return getSectionOrCreate(sectionPath).getKeys(deep);
    }

    public void setComments(@NotNull String path, @NotNull List<String> comments) {
        backend.setComments(path, comments);
    }

    public void setDefaults(@NotNull Configuration defaults) {
        backend.setDefaults(defaults);
    }

    public @NotNull YamlConfigurationOptions options() {
        return backend.options();
    }

    public void save() {
        save(file);
    }

    public void save(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file is not set");
        }

        try {
            backend.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
