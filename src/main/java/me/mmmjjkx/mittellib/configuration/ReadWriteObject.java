package me.mmmjjkx.mittellib.configuration;

import com.google.common.base.Preconditions;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unchecked")
public abstract class ReadWriteObject {
    private static final Constructor<? extends ReadWriteObject> CONSTRUCTOR;

    static {
        try {
            CONSTRUCTOR = ReadWriteObject.class.getConstructor(ConfigurationSection.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends ReadWriteObject> T read(@NotNull Class<T> clazz, @NotNull ConfigurationSection cs) {
        Preconditions.checkNotNull(clazz);

        try {
            return (T) CONSTRUCTOR.newInstance(cs);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public ReadWriteObject() {
    }

    public ReadWriteObject(ConfigurationSection cs) {
        read(cs);
    }

    public abstract void write(ConfigurationSection cs);

    public abstract void read(ConfigurationSection cs);
}
