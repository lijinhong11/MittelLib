package io.github.lijinhong11.mittellib.configuration;

import com.google.common.base.Preconditions;
import io.github.lijinhong11.mittellib.MittelLib;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReadWriteObject {
    static @Nullable <T extends ReadWriteObject> T read(@NotNull Class<T> clazz, @NotNull ConfigurationSection cs) {
        Preconditions.checkNotNull(clazz);

        try {
            return clazz.getConstructor(ConfigurationSection.class).newInstance(cs);
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            MittelLib.getInstance().getLogger().log(Level.SEVERE, "Failed to instance " + clazz.getName(), e);
        }

        return null;
    }

    void write(ConfigurationSection cs);

    void read(ConfigurationSection cs);
}
