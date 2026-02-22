package io.github.lijinhong11.mittellib.configuration;

import io.github.lijinhong11.mittellib.item.MittelItem;
import io.github.lijinhong11.mittellib.utils.ComponentUtils;
import io.github.lijinhong11.mittellib.utils.EnumUtils;
import io.github.lijinhong11.mittellib.utils.NumberUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MittelConfig {
    private YamlConfiguration backend;

    @Setter
    @Getter
    private File file;

    public MittelConfig() {
        this.backend = new YamlConfiguration();
    }

    public MittelConfig(YamlConfiguration configuration) {
        this.backend = configuration;
    }

    private MittelConfig(@NotNull File file, @NotNull YamlConfiguration configuration) {
        this.file = file;
        this.backend = configuration;
    }

    public static @NotNull MittelConfig load(@NotNull File file, boolean createIfMissing) {
        if (!file.exists()) {
            if (!createIfMissing) {
                throw new IllegalStateException("Config file does not exist: " + file.getPath());
            }

            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create config file", e);
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return new MittelConfig(file, config);
    }

    public void reload() {
        ensureFile();
        this.backend = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        ensureFile();
        try {
            backend.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config", e);
        }
    }

    private void ensureFile() {
        if (file == null) {
            throw new IllegalStateException("Config file is not set");
        }
    }

    public boolean contains(@NotNull String path) {
        return backend.contains(path);
    }

    public void remove(@NotNull String path) {
        backend.set(path, null);
    }

    public void clear() {
        for (String key : backend.getKeys(false)) {
            backend.set(key, null);
        }
    }

    public @Nullable String getString(@NotNull String path) {
        return backend.getString(path);
    }

    public @Nullable String getString(@NotNull String path, @Nullable String def) {
        return backend.getString(path, def);
    }

    public @Nullable Component getComponent(@NotNull String path) {
        return getComponent(path, null);
    }

    public @Nullable Component getComponent(@NotNull String path, @Nullable Component def) {
        String s = getString(path);
        if (s == null) {
            return def;
        }

        return ComponentUtils.deserialize(s);
    }

    public @NotNull String requireString(@NotNull String path) {
        String value = getString(path);
        if (value == null) {
            throw new IllegalStateException("Missing required config path: " + path);
        }
        return value;
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

    public boolean getBoolean(@NotNull String path) {
        return backend.getBoolean(path);
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        return backend.getBoolean(path, def);
    }

    public @NotNull List<String> getStringList(@NotNull String path) {
        return backend.getStringList(path);
    }

    public int getUnsignedInt(String path) {
        return NumberUtils.asUnsigned(getInt(path));
    }

    public long getUnsignedLong(String path) {
        return NumberUtils.asUnsigned(getLong(path));
    }

    public double getUnsignedDouble(String path) {
        return NumberUtils.asUnsigned(getDouble(path));
    }

    public float getUnsignedFloat(String path) {
        return NumberUtils.asUnsigned(getFloat(path));
    }

    public boolean isSection(@NotNull String path) {
        return backend.isConfigurationSection(path);
    }

    public @Nullable ConfigurationSection getSection(@NotNull String path) {
        return backend.getConfigurationSection(path);
    }

    public @NotNull ConfigurationSection getSectionOrCreate(@NotNull String path) {
        ConfigurationSection cs = getSection(path);
        return cs == null ? backend.createSection(path) : cs;
    }

    public Set<String> getKeys(boolean deep) {
        return backend.getKeys(deep);
    }

    public Set<String> getKeys(@NotNull String path, boolean deep) {
        ConfigurationSection cs = getSection(path);
        return cs == null ? Collections.emptySet() : cs.getKeys(deep);
    }

    public <E extends Enum<E>> @Nullable E getEnum(@NotNull String path, @NotNull Class<E> clazz) {
        String value = getString(path);
        if (value == null) return null;

        return EnumUtils.readEnum(clazz, value);
    }

    public <E extends Enum<E>> @NotNull E getEnum(@NotNull String path, @NotNull Class<E> clazz, @NotNull E def) {
        E e = getEnum(path, clazz);
        return e == null ? def : e;
    }

    public @Nullable ItemStack getItemStack(@NotNull String path) {
        ConfigurationSection cs = getSection(path);
        if (cs == null) return null;

        try {
            MittelItem item = MittelItem.readFromSection(cs);
            return item.get();
        } catch (Exception e) {
            return null;
        }
    }

    public <T extends ReadWriteObject> @Nullable T getRWObject(
            @NotNull String path,
            @NotNull Class<T> clazz
    ) {
        ConfigurationSection cs = getSection(path);
        if (cs == null) return null;

        return ReadWriteObject.read(clazz, cs);
    }

    public void setDefaults(@NotNull Configuration defaults) {
        backend.setDefaults(defaults);
    }

    public YamlConfigurationOptions options() {
        return backend.options();
    }
}